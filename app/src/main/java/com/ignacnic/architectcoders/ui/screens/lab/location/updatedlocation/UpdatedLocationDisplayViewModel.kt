package com.ignacnic.architectcoders.ui.screens.lab.location.updatedlocation

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ignacnic.architectcoders.domain.location.LocationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation

class UpdatedLocationDisplayViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    fun onStopUpdates() {
        locationRepository.removeLocationUpdates()
        state = state.copy(updatesRunning = false)
    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun onPermissionsRequestResult(permissionStates: Map<String, Boolean>) {
        when {
            permissionStates.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                startRequest()
            }

            else -> {
                state = state.copy(updatesRunning = false)
            }
        }
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    private fun startRequest() {
        state = state.copy(
            updatesRunning = true,
        )
        locationRepository.requestLocationUpdates { locations ->
            state = state.copy(
                locationUpdates = state.locationUpdates.plus(locations)
            )
        }
    }


    data class UiState(
        val locationUpdates: List<MyLocation>,
        val updatesRunning: Boolean,
    )

    var state by mutableStateOf(
        UiState(
            locationUpdates = listOf(),
            updatesRunning = false,
        )
    )
        private set
}