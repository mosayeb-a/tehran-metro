package com.ma.tehro.feature.map.city

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StationsOnCityMap(
    onFindCurrentLocationClick: () -> Unit,
    viewState: MapUiState,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (!viewState.isLocating) onFindCurrentLocationClick() },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                if (!viewState.isLocating) {
                    Icon(
                        imageVector = Icons.Rounded.MyLocation,
                        contentDescription = "Find current location",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
    ){
        StationsMap(
            viewState = viewState,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        )
    }
}