package com.ma.tehro.ui.map

import android.location.Location
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.data.Station
import com.ma.tehro.services.LocationClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class MapUiState(
    var currentLocation: Location? = null,
    val isLoading: Boolean = false,
    val stations: Map<String, Station> = emptyMap()
)

@HiltViewModel
class StationsMapViewModel @Inject constructor(
    private val locationClient: LocationClient,
    private val stations: Map<String, Station>
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> get() = _uiState

    init {
        _uiState.update { it.copy(stations = stations) }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val location = locationClient.getCurrentLocation()
                println("StationsMap current location received -> lat: ${location.latitude}, long: ${location.longitude}")
                _uiState.value.currentLocation?.let {
                    if (location.latitude == it.latitude && location.longitude == it.longitude) {
                        return@launch
                    }
                }

                _uiState.update { state ->
                    state.copy(
                        currentLocation = location,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                println("Error getting current location: ${e.message}")
            }
        }
    }
}