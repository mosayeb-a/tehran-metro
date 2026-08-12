package com.ma.tehro.feature.map.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.Nearby
import com.ma.tehro.domain.path.NearbyFinder
import com.ma.tehro.services.LocationClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapUiState(
    val zoom: Double = 11.0,
    val stations: List<StationMarker> = emptyList(),
    val isLocating: Boolean = false,
    val myLocationLat: Double? = null,
    val myLocationLon: Double? = null,
    val nearby: List<Nearby<Station>> = emptyList(),
)

class StationsMapViewModel(
    private val locationClient: LocationClient,
    stations: Map<String, Station>
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MapUiState(
            stations = stations.values.map { station ->
                StationMarker(
                    lat = station.latitude,
                    lon = station.longitude,
                    name = BilingualName(en = station.name, fa = station.translations.fa),
                    lines = station.lines
                )
            }.filter { it.lat != 0.0 && it.lon != 0.0 }
        )
    )
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val stationFinder: NearbyFinder<Station> by lazy {
        NearbyFinder(stations.values.toList())
    }

    fun locateMe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocating = true) }
            try {
                val location = locationClient.getCurrentLocation()
                _uiState.update { current ->
                    current.copy(
                        myLocationLat = location.latitude,
                        myLocationLon = location.longitude,
                        zoom = 15.0,
                        isLocating = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLocating = false) }
            }
        }
    }

    fun findNearbyStations(lat: Double, lon: Double) {
        viewModelScope.launch {
            val stations = stationFinder.find(lat, lon, limit = 3)
            _uiState.update { current ->
                current.copy(
                    nearby = stations,
                    myLocationLat = lat,
                    myLocationLon = lon
                )
            }
        }
    }
}