package com.ignacnic.architectcoders.ui.location.requester

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import com.ignacnic.architectcoders.domain.location.LocationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class LocationRequesterScreenViewModel(
    private val locationRepository: LocationRepository
) : ViewModel(){

    sealed interface Action {
        data class PermissionResult(val permissionStates: Map<String, Boolean>): Action
        data object UpdatesStopped: Action
    }

    data class UiState(
        val locationUpdates: List<MyLocation>,
        val updatesRunning: Boolean,
    )

    var state = MutableStateFlow(
        UiState(
            locationUpdates = listOf(),
            updatesRunning = false,
        )
    )
        private set

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun reduceAction(action: Action) {
        when (action) {
            is Action.PermissionResult -> onPermissionsRequestResult(action.permissionStates)
            is Action.UpdatesStopped -> onStopUpdates()
        }
    }

    private fun onStopUpdates() {
        locationRepository.removeLocationUpdates()
        state.update{ it.copy(updatesRunning = false) }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun onPermissionsRequestResult(permissionStates: Map<String, Boolean>) {
        when {
            permissionStates.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                startRequest()
            }

            else -> {
                state.update{ it.copy(updatesRunning = false) }
            }
        }
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    private fun startRequest() {
        state.update {
            it.copy(
                updatesRunning = true,
            )
        }
        locationRepository.requestLocationUpdates { locations ->
            state.update {
                it.copy(
                    locationUpdates = it.locationUpdates.plus(locations)
                )
            }
        }
    }
}
