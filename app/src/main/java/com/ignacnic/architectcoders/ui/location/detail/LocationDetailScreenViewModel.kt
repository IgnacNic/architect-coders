package com.ignacnic.architectcoders.ui.location.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignacnic.architectcoders.domain.elevation.ElevationRepository
import com.ignacnic.architectcoders.domain.location.MyLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationDetailScreenViewModel(
    private val location: MyLocation,
    private val elevationRepository: ElevationRepository,
) : ViewModel() {

    data class UiState(
        val latitude: String,
        val longitude: String,
        val time: String,
        val loading: Boolean,
        val elevation: Double?,
    )

    private val _state = MutableStateFlow(
        UiState(
            latitude = location.latitude,
            longitude = location.longitude,
            time = location.timeStamp,
            loading = true,
            elevation = null,
        )
    )
    val state = _state.asStateFlow()


    sealed interface Action {
        data object onUiLoad: Action
    }

    fun reduceAction(action: Action) {
        when (action) {
            is Action.onUiLoad -> {
                requestElevation()
            }
        }
    }

    private fun requestElevation() {
        _state.update {
            it.copy(
                elevation = null,
                loading = true
            )
        }
        viewModelScope.launch {
            val elevation = elevationRepository.getElevationForLocations(
                locations = listOf(location)
            ).firstOrNull()
            _state.update {
                it.copy(
                    elevation = elevation,
                    loading = false,
                )
            }
        }
    }
}
