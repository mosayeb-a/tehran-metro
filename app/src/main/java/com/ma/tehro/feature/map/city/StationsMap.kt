package com.ma.tehro.feature.map.city

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.ma.tehro.R
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.common.osm_map.CameraProperty
import com.ma.tehro.common.osm_map.CameraState
import com.ma.tehro.common.osm_map.DefaultMapProperties
import com.ma.tehro.common.osm_map.InfoWindowData
import com.ma.tehro.common.osm_map.Marker
import com.ma.tehro.common.osm_map.OpenStreetMap
import com.ma.tehro.common.osm_map.ZoomButtonVisibility
import com.ma.tehro.common.osm_map.rememberMarkerState
import com.ma.tehro.common.osm_map.rememberOverlayManagerState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

@Composable
fun StationsMap(
    onFindCurrentLocationClick: () -> Unit,
    viewState: MapUiState,
) {
    var cameraState by remember {
        mutableStateOf(
            CameraState(
                CameraProperty(
                    geoPoint = GeoPoint(35.68891662087582, 51.390395164489746),
                    zoom = 14.0
                )
            )
        )
    }

    val currentLocation = viewState.currentLocation

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraState = CameraState(
                CameraProperty(
                    geoPoint = GeoPoint(it.latitude, it.longitude),
                    zoom = 15.0
                )
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (!viewState.isLoading) onFindCurrentLocationClick() },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                if (!viewState.isLoading) {
                    Icon(
                        painter = painterResource(R.drawable.my_location_24px),
                        contentDescription = "Find current location",
                        tint = Color.White
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        val context = LocalContext.current
        val mapProperties = remember {
            DefaultMapProperties.copy(
                isTilesScaledToDpi = true,
                tileSources = TileSourceFactory.MAPNIK,
                isEnableRotationGesture = true,
                zoomButtonVisibility = ZoomButtonVisibility.NEVER
            )
        }

        val overlayManagerState = rememberOverlayManagerState()

        val stationMarkers = viewState.stations.values.mapNotNull { station ->
            val lat = station.latitude?.toDoubleOrNull()
            val lon = station.longitude?.toDoubleOrNull()
            if (lat != null && lon != null) {
                Pair(GeoPoint(lat, lon), station)
            } else {
                null
            }
        }

        val markerStates = stationMarkers.map { (geoPoint, _) ->
            rememberMarkerState(geoPoint = geoPoint)
        }
        val currentLocationMarkerState = currentLocation?.let {
            rememberMarkerState(geoPoint = GeoPoint(it.latitude, it.longitude))
        }

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OpenStreetMap(
                modifier = Modifier.fillMaxSize(),
                cameraState = cameraState,
                properties = mapProperties,
                overlayManagerState = overlayManagerState,
                onMapLongClick = {
                    println(it)
                },
                onFirstLoadListener = {},
            ) {
                val markerIcon: Drawable? =
                    ContextCompat.getDrawable(context, R.drawable.location_on_24px)
                stationMarkers.forEachIndexed { index, (_, station) ->
                    Marker(
                        icon = markerIcon,
                        state = markerStates[index],
                        title = createBilingualMessage(
                            fa = station.translations.fa,
                            en = station.name
                        ),
                        infoWindowContent = { data ->
                            StationInfoWindow(data)
                        }
                    )
                }

                currentLocationMarkerState?.let { state ->
                    Marker(
                        state = state,
                        title = "Your Location",
                        infoWindowContent = { data ->
                            StationInfoWindow(data)
                        }
                    )
                }
            }

            Text(
                text = "© OpenStreetMap contributors",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://www.openstreetmap.org/copyright".toUri()
                        )
                        context.startActivity(intent)
                    },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun StationInfoWindow(data: InfoWindowData) {
    Surface(
        color = Color.White,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = data.title,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


