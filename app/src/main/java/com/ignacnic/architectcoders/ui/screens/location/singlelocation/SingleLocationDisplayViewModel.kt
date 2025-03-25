package com.ignacnic.architectcoders.ui.screens.location.singlelocation

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignacnic.architectcoders.domain.LocationRepository
import com.ignacnic.architectcoders.domain.MyLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SingleLocationDisplayViewModel(
    private val locationRepository: LocationRepository,
) : ViewModel() {

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
        state.update { it.copy(loading = true) }
        viewModelScope.launch {
            locationRepository.requestSingleLocation { location ->
                state.update{
                    it.copy(
                        location = location,
                        locationFetched = true,
                        loading = false
                    )
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun handleIntent(action: Intent) {
        when(action) {
            is Intent.PermissionResult -> {
                onPermissionsRequestResult(action.permissionStates)
            }
        }
    }

    sealed interface Intent {
        data class PermissionResult(val permissionStates: Map<String, Boolean>): Intent
    }

    var state = MutableStateFlow(UIState(
        location = null,
        locationFetched = false,
        loading = false,
    ))
        private set

    data class UIState(
        val location: MyLocation?,
        val locationFetched: Boolean,
        val loading: Boolean
    )
}