package com.ma.tehro.feature.map.city

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun StationsMap(
    modifier: Modifier,
    viewState: MapUiState,
    isSelection: Boolean,
    onMarkerCenterChanged: ((Double, Double) -> Unit)?,
) {
}