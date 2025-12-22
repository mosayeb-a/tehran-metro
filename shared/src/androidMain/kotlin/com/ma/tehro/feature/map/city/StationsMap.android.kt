package com.ma.tehro.feature.map.city

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.shared.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

@Composable
actual fun StationsMap(
    viewState: MapUiState,
    modifier: Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setBuiltInZoomControls(false)
                setMultiTouchControls(true)

                isTilesScaledToDpi = true
                isHorizontalMapRepetitionEnabled = false
                isVerticalMapRepetitionEnabled = false

                controller.setZoom(11.0)
                controller.setCenter(GeoPoint(35.6892, 51.3890))

                overlays.clear()
            }
        },
        update = { mapView ->
            if (viewState.centerLat != null && viewState.centerLon != null) {
                mapView.controller.animateTo(
                    GeoPoint(viewState.centerLat, viewState.centerLon),
                    15.0,
                    1000L
                )
            }

            mapView.overlays.clear()

            val stationIcon: Drawable? =
                ContextCompat.getDrawable(context, R.drawable.location_on_24px)

            viewState.markers.forEach { marker ->
                val geoPoint = GeoPoint(marker.lat, marker.lon)
                val osMarker = Marker(mapView).apply {
                    position = geoPoint
                    title = createBilingualMessage(fa = marker.titleFa, en = marker.title)
                    icon = stationIcon
                    infoWindow = StationInfoWindow(mapView, this)
                }
                mapView.overlays.add(osMarker)
            }

            viewState.centerLat?.let { lat ->
                viewState.centerLon?.let { lon ->
                    val myMarker = Marker(mapView).apply {
                        position = GeoPoint(lat, lon)
                        title = "موقعیت شما\nYour Location"
                        infoWindow = StationInfoWindow(mapView, this)
                    }
                    mapView.overlays.add(myMarker)
                }
            }

            mapView.invalidate()
        },
        onRelease = { mapView ->
            mapView.onDetach()
        },
        modifier = modifier.fillMaxSize()
    )

    Box(
        modifier = Modifier
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
            )
            .fillMaxSize()
    ) {
        Text(
            text = "© OpenStreetMap contributors",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.openstreetmap.org/copyright".toUri()
                    )
                    context.startActivity(intent)
                }
                .padding(2.dp ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

class StationInfoWindow(mapView: MapView, val myMarker: Marker) :
    InfoWindow(R.layout.bubble, mapView) {
    override fun onOpen(item: Any) {
        val bubble = mView.findViewById<View>(R.id.bubble_title)
        if (bubble is android.widget.TextView) {
            bubble.text = myMarker.title
        }
    }

    override fun onClose() {}
}