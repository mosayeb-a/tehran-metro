package com.ma.tehro.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ma.tehro.common.osm_map.OpenStreetMap
import com.ma.tehro.common.osm_map.rememberCameraState
import org.osmdroid.util.GeoPoint

@Composable
fun StationsMap(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) {
        val cameraState = rememberCameraState {
            // Atmosphere station geo point
            geoPoint = GeoPoint(35.7662914, 51.0469759)
            zoom = 12.0
        }

        OpenStreetMap(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            cameraState = cameraState
        )
    }
}