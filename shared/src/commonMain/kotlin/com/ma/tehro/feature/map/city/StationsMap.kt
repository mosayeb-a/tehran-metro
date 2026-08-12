package com.ma.tehro.feature.map.city

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ma.tehro.domain.common.BilingualName

data class StationMarker(
    val lat: Double,
    val lon: Double,
    val name : BilingualName,
    val lines: List<Int>
)

@Composable
expect fun StationsMap(
    modifier: Modifier = Modifier,
    viewState: MapUiState,
    isSelection: Boolean = false,
    onMarkerCenterChanged: ((Double, Double) -> Unit)?,
)

