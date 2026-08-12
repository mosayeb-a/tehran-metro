package com.ma.tehro.feature.map.city

import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.shared.R
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.infowindow.InfoWindow

@Composable
actual fun StationsMap(
    modifier: Modifier,
    viewState: MapUiState,
    isSelection: Boolean,
    onMarkerCenterChanged: ((Double, Double) -> Unit)?,
) {
    val context = LocalContext.current
    val redMarkerIcon: Drawable? = remember {
        ContextCompat.getDrawable(context, R.drawable.map_marker_red)
    }

    val onCenterChanged by rememberUpdatedState(onMarkerCenterChanged)

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                isTilesScaledToDpi = true
                setTilesScaleFactor(1f)
                setBuiltInZoomControls(false)
                setMultiTouchControls(true)
                isHorizontalMapRepetitionEnabled = false
                isVerticalMapRepetitionEnabled = false
                controller.setZoom(14.0)
                controller.setCenter(GeoPoint(35.6892, 51.3890))

                addMapListener(object : MapListener {
                    override fun onScroll(event: ScrollEvent?): Boolean {
                        val centerX = width / 2
                        val centerY = height / 2
                        val center = projection.fromPixels(centerX, centerY)
                        onCenterChanged?.invoke(center.latitude, center.longitude)
                        return true
                    }

                    override fun onZoom(event: ZoomEvent?): Boolean {
                        val centerX = width / 2
                        val centerY = height / 2
                        val center = projection.fromPixels(centerX, centerY)
                        onCenterChanged?.invoke(center.latitude, center.longitude)
                        return true
                    }
                })

                overlays.clear()
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            viewState.stations.forEach { marker ->
                val geoPoint = GeoPoint(marker.lat, marker.lon)
                val stationIcon = StationMarkerDrawable(marker.lines)

                val osMarker = Marker(mapView).apply {
                    position = geoPoint
                    title = createBilingualMessage(fa = marker.name.fa, en = marker.name.en)
                    icon = stationIcon

                    setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    )

                    infoWindow = StationInfoWindow(mapView, this)
                }
                mapView.overlays.add(osMarker)
            }

            val centerOverlay = object : Overlay() {
                override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
                    if (shadow) return
                    val projection: Projection = mapView.projection
                    val centerX = mapView.width / 2
                    val centerY = mapView.height / 2
                    val centerGeoPoint = projection.fromPixels(centerX, centerY)
                    val screenPoint = projection.toPixels(centerGeoPoint, null)
                    redMarkerIcon?.let { drawable ->
                        drawable.setBounds(
                            screenPoint.x - drawable.intrinsicWidth / 2,
                            screenPoint.y - drawable.intrinsicHeight,
                            screenPoint.x + drawable.intrinsicWidth / 2,
                            screenPoint.y
                        )
                        drawable.draw(canvas)
                    }
                }
            }

            if (isSelection) {
                mapView.overlays.add(centerOverlay)
            }

            if (viewState.myLocationLat != null && viewState.myLocationLon != null) {
                mapView.overlays.add(centerOverlay)
                mapView.controller.animateTo(
                    GeoPoint(viewState.myLocationLat, viewState.myLocationLon),
                    15.0,
                    1000L
                )
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
            .padding(horizontal = 4.dp)
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
            )
            .fillMaxSize()
    ) {
        Text(
            text = "© OpenStreetMap contributors",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.openstreetmap.org/copyright".toUri()
                    )
                    context.startActivity(intent)
                },
            style = MaterialTheme.typography.labelSmall,
            color = Color.Black.copy(alpha = .6f)
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