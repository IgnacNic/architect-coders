package com.ignacnic.architectcoders.feature.location.requester

import android.Manifest
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignacnic.architectcoders.domain.gpxfile.domain.GPXFileRepository
import com.ignacnic.architectcoders.domain.location.domain.LocationRepository
import com.ignacnic.architectcoders.domain.userpreferences.domain.PreferencesKeys
import com.ignacnic.architectcoders.domain.userpreferences.domain.UserPreferencesRepository
import com.ignacnic.architectcoders.entities.buildconfig.BuildConfigFieldsProvider
import com.ignacnic.architectcoders.entities.location.MyLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationRequesterScreenViewModel(
    private val locationRepository: LocationRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val gpxFileRepository: GPXFileRepository,
    private val buildConfigFieldsProvider: BuildConfigFieldsProvider,
) : ViewModel(){

    sealed interface Action {
        data class PermissionResult(val permissionStates: Map<String, Boolean>): Action
        data class CardClicked(val location: MyLocation) : Action
        data class FileDirectoryPicked(val result: Uri) : Action
        data class FileCreated(val result: Uri) : Action
        data object UpdatesStopped: Action
        data object RationaleDialogDismissed: Action
        data object RationaleDialogConfirmed: Action
        data object TrashDialogDismissed : Action
        data object TrashUpdatesConfirmed : Action
        data object TrashUpdatesRequested : Action
        data object SaveRoute : Action
    }

    data class UiState(
        val locationUpdates: List<MyLocation>,
        val updatesRunning: Boolean,
        val locationRationaleNeeded: Boolean,
        val updatesTrashRequested: Boolean,
    )

    sealed interface SideEffect {
        data class NavigateToLocationDetails(val location: MyLocation) : SideEffect
        data class LaunchAppDetailsSettings(val appId: String) : SideEffect
        data object LaunchDirectoryPicker : SideEffect
        data class LaunchFilePicker(val uri: Uri) : SideEffect
        data class WriteToFile(val uri: Uri, val content: String) : SideEffect
    }

    private val _state = MutableStateFlow(
        UiState(
            locationUpdates = listOf(),
            updatesRunning = false,
            locationRationaleNeeded = false,
            updatesTrashRequested = false,
        )
    )
    val state = _state.asStateFlow()

    private val _sideEffects by lazy { Channel<SideEffect>() }
    val sideEffects = _sideEffects.receiveAsFlow().distinctUntilChanged()

    private var locationUpdatesJob: Job? = null

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun reduceAction(action: Action) {
        when (action) {
            is Action.PermissionResult -> onPermissionsRequestResult(action.permissionStates)
            is Action.UpdatesStopped -> onStopUpdates()
            is Action.RationaleDialogDismissed -> onRationaleDismissed()
            is Action.RationaleDialogConfirmed -> onRationaleConfirmed()
            is Action.TrashDialogDismissed -> onTrashDialogDismissed()
            is Action.TrashUpdatesRequested -> onTrashUpdatesRequested()
            is Action.TrashUpdatesConfirmed -> onTrashUpdatesConfirmed()
            is Action.SaveRoute -> onSaveClicked()
            is Action.CardClicked -> onLocationCardClicked(action.location)
            is Action.FileDirectoryPicked -> onFileDirectoryPicked(action.result)
            is Action.FileCreated -> onFileCreated(action.result)
        }
    }

    private fun onStopUpdates() {
        locationRepository.stopLocationUpdates()
        _state.update{ it.copy(updatesRunning = false) }
        locationUpdatesJob?.let {
            it.cancel()
            locationUpdatesJob = null
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun onPermissionsRequestResult(permissionStates: Map<String, Boolean>) {
        when {
            permissionStates.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                startRequest()
            }

            else -> {
                _state.update{
                    it.copy(
                        updatesRunning = false,
                        locationRationaleNeeded = true,
                    )
                }
            }
        }
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    private fun startRequest() {
        locationUpdatesJob?.run { throw IllegalStateException("Location updates are already running") }
        _state.update {
            it.copy(
                updatesRunning = true,
                locationRationaleNeeded = false,
            )
        }
        locationUpdatesJob = viewModelScope.launch {
            locationRepository.startLocationUpdates().collect { locations ->
                _state.update {
                    it.copy(
                        locationUpdates = it.locationUpdates.plus(locations)
                    )
                }
            }
        }
    }

    private fun onRationaleDismissed() {
        _state.update { it.copy(locationRationaleNeeded = false) }
    }

    private fun onRationaleConfirmed() {
        _state.update { it.copy(locationRationaleNeeded = false) }
        viewModelScope.launch {
            _sideEffects.send(
                SideEffect.LaunchAppDetailsSettings(
                    appId = buildConfigFieldsProvider.getBuildConfig().appId,
                ),
            )
        }
    }

    private fun onTrashDialogDismissed() {
        _state.update { it.copy(updatesTrashRequested = false) }
    }

    private fun onTrashUpdatesRequested() {
        _state.update { it.copy(updatesTrashRequested = true) }
    }

    private fun onTrashUpdatesConfirmed() {
        locationRepository.stopLocationUpdates()
        _state.update {
            it.copy(
                updatesTrashRequested = false,
                updatesRunning = false,
                locationUpdates = emptyList(),
            )
        }
    }

    private fun onSaveClicked() {
        _state.update { it.copy(updatesRunning = false) }
        val storingUri = userPreferencesRepository.getString(
            key = PreferencesKeys.FILE_STORAGE_URI.key,
            default = ""
        )
        viewModelScope.launch {
            _sideEffects.send(
                if (storingUri == "") {
                    SideEffect.LaunchDirectoryPicker
                } else {
                    SideEffect.LaunchFilePicker(storingUri.toUri())
                }
            )
        }
    }

    private fun onFileDirectoryPicked(uri: Uri) {
        userPreferencesRepository.putString(
            key = PreferencesKeys.FILE_STORAGE_URI.key,
            value = uri.toString(),
        )
        viewModelScope.launch {
            _sideEffects.send(SideEffect.LaunchFilePicker(uri))
        }
    }

    private fun onFileCreated(uri: Uri) {
        val content = gpxFileRepository.getGPXFromLocations(
            locations = state.value.locationUpdates,
            fileName = uri.lastPathSegment ?: "",
        )
        viewModelScope.launch {
            _sideEffects.send(SideEffect.WriteToFile(uri, content))
        }
    }

    private fun onLocationCardClicked(location: MyLocation) {
        viewModelScope.launch {
            _sideEffects.send(SideEffect.NavigateToLocationDetails(location))
        }
    }
}
