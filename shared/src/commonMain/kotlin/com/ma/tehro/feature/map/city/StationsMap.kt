package com.ma.tehro.feature.map.city

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class MapMarker(
    val lat: Double,
    val lon: Double,
    val title: String,
    val titleFa: String,
    val line: Int? = null
)

@Composable
expect fun StationsMap(
    viewState: MapUiState,
    modifier: Modifier = Modifier
)

