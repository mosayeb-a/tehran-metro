package com.ma.tehro.feature.map.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.data.Station
import com.ma.tehro.services.LocationClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapUiState(
    val centerLat: Double? = null,
    val centerLon: Double? = null,
    val zoom: Double = 11.0,
    val markers: List<MapMarker> = emptyList(),
    val isLocating: Boolean = false
)

class StationsMapViewModel(
    private val locationClient: LocationClient,
    stations: Map<String, Station>
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MapUiState(
            markers = stations.values.map { station ->
                MapMarker(
                    lat = station.latitude?.toDoubleOrNull() ?: 0.0,
                    lon = station.longitude?.toDoubleOrNull() ?: 0.0,
                    title = station.name,
                    titleFa = station.translations.fa,
                    line = station.lines.firstOrNull()
                )
            }.filter { it.lat != 0.0 && it.lon != 0.0 } // filter invalid coords
        )
    )
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun locateMe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocating = true) }

            try {
                val location = locationClient.getCurrentLocation()

                _uiState.update { current ->
                    current.copy(
                        centerLat = location.latitude,
                        centerLon = location.longitude,
                        zoom = 15.0,
                        isLocating = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLocating = false) }
                println("Location error: ${e.message}")
            }
        }
    }
}