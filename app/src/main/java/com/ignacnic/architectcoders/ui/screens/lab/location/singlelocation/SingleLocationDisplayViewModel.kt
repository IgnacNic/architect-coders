package com.ignacnic.architectcoders.ui.screens.lab.location.singlelocation

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignacnic.architectcoders.domain.ElevationRepository
import com.ignacnic.architectcoders.domain.location.LocationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SingleLocationDisplayViewModel(
    private val locationRepository: LocationRepository,
    private val elevationRepository: ElevationRepository,
) : ViewModel() {

    sealed interface Intent {
        data class PermissionResult(val permissionStates: Map<String, Boolean>): Intent
        data object RequestElevation: Intent
    }

    var state = MutableStateFlow(
        UIState(
        location = null,
        locationFetched = false,
        locationLoading = false,
        elevationLoading = false,
        elevation = null,
    )
    )
        private set

    data class UIState(
        val location: MyLocation?,
        val locationFetched: Boolean,
        val locationLoading: Boolean,
        val elevationLoading: Boolean,
        val elevation: Double?,
    )

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun handleIntent(action: Intent) {
        when(action) {
            is Intent.PermissionResult -> {
                onPermissionsRequestResult(action.permissionStates)
            }
            is Intent.RequestElevation -> {
                onElevationRequest()
            }
        }
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    private fun onPermissionsRequestResult(permissionStates: Map<String, Boolean>) {
        when {
            permissionStates.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                fetchAccurateLocation()
            }

            else -> {
                state.update {
                    it.copy(
                        location = null,
                        locationFetched = true
                    )
                }
            }
        }
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )
    private fun fetchAccurateLocation() {
        state.update { it.copy(locationLoading = true) }
        viewModelScope.launch {
            locationRepository.requestSingleLocation { location ->
                state.update{
                    it.copy(
                        location = location,
                        locationFetched = true,
                        locationLoading = false,
                        elevation = null,
                    )
                }
            }
        }
    }

    private fun onElevationRequest() {
        state.value.location?.let { location ->
            state.update {
                it.copy(
                    elevation = null,
                    locationFetched = true
                )
            }
            viewModelScope.launch {
                val elevation = elevationRepository.getElevationForLocations(
                    locations = listOf(location)
                ).firstOrNull()
                state.update {
                    it.copy(
                        elevation = elevation,
                        elevationLoading = false,
                    )
                }
            }
        }
    }
}