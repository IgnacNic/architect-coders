package com.ignacnic.architectcoders.ui.location.requester

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.ignacnic.architectcoders.domain.location.LocationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocationRequesterScreenViewModel(
    private val locationRepository: LocationRepository
) : ViewModel(){

    sealed interface Action {
        data class PermissionResult(val permissionStates: Map<String, Boolean>): Action
        data object UpdatesStopped: Action
        data object RationaleDialogDismissed: Action
        data object RationaleDialogConfirmed: Action
    }

    data class UiState(
        val locationUpdates: List<MyLocation>,
        val updatesRunning: Boolean,
        val locationRationaleNeeded: Boolean,
    )

    private val _state = MutableStateFlow(
        UiState(
            locationUpdates = listOf(),
            updatesRunning = false,
            locationRationaleNeeded = false,
        )
    )
    val state = _state.asStateFlow()

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun reduceAction(action: Action) {
        when (action) {
            is Action.PermissionResult -> onPermissionsRequestResult(action.permissionStates)
            Action.UpdatesStopped -> onStopUpdates()
            Action.RationaleDialogDismissed -> onRationaleDismissed()
            Action.RationaleDialogConfirmed -> onRationaleConfirmed()
        }
    }

    private fun onStopUpdates() {
        locationRepository.removeLocationUpdates()
        _state.update{ it.copy(updatesRunning = false) }
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
        _state.update {
            it.copy(
                updatesRunning = true,
                locationRationaleNeeded = false,
            )
        }
        locationRepository.requestLocationUpdates { locations ->
            _state.update {
                it.copy(
                    locationUpdates = it.locationUpdates.plus(locations)
                )
            }
        }
    }

    private fun onRationaleDismissed() {
        _state.update { it.copy(locationRationaleNeeded = false) }
    }

    private fun onRationaleConfirmed() {
        _state.update { it.copy(locationRationaleNeeded = false) }
    }
}
