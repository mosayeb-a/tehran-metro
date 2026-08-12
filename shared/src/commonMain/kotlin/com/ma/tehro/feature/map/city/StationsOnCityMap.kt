package com.ma.tehro.feature.map.city

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.feature.map.city.components.MapFabContainer
import com.ma.tehro.feature.map.city.components.MapTopBar
import com.ma.tehro.feature.map.city.components.NearbyStationsSheet

@Composable
fun StationsOnCityMap(
    viewState: MapUiState,
    isStationSelection: Boolean,
    onFindMyLocation: () -> Unit,
    onFindNearby: ((lat: Double, long: Double) -> Unit),
    onStationSelected: (en: String, fa: String) -> Unit,
) {
    var pinLat by remember { mutableStateOf(viewState.myLocationLat ?: 35.6892) }
    var pinLon by remember { mutableStateOf(viewState.myLocationLon ?: 51.3890) }
    var showNearby by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { if (isStationSelection) MapTopBar() },
        floatingActionButton = {
            MapFabContainer(
                isSelectionMode = isStationSelection,
                isLocating = viewState.isLocating,
                onFindCurrentLocationClick = onFindMyLocation,
                onConfirmLocation = {
                    onFindNearby(pinLat, pinLon)
                    showNearby = true
                },
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            StationsMap(
                viewState = viewState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                onMarkerCenterChanged = { lat, lon ->
                    pinLat = lat
                    pinLon = lon
                },
                isSelection = isStationSelection
            )
        }
    }

    if (showNearby && viewState.nearby.isNotEmpty()) {
        NearbyStationsSheet(
            nearbyStations = viewState.nearby,
            onStationSelected = { station ->
                onStationSelected(station.name, station.translations.fa)
                showNearby = false
            },
            onDismiss = { showNearby = false },
            onRetry = { onFindNearby(pinLat, pinLon) }
        )
    }
}