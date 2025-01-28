package com.ma.tehro.common.osm_map

import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

enum class PolylineCap {
    BUTT, ROUND, SQUARE
}

@Composable
@OsmAndroidComposable
fun Polyline(
    geoPoints: List<GeoPoint>,
    color: Color = Color.Black,
    width: Float = 12f,
    cap: PolylineCap = PolylineCap.SQUARE,
    visible: Boolean = true,
    onClick: (Polyline) -> Unit = {},
    title: String? = null,
    snippet: String? = null,
    id: String? = null,
    onPolylineLoaded: (Paint) -> Unit = {},
    infoWindowContent: @Composable (InfoWindowData) -> Unit = {}
) {

    val context = LocalContext.current
    val applier =
        currentComposer.applier as? MapApplier ?: throw IllegalStateException("Invalid Applier")

    ComposeNode<PolylineNode, MapApplier>(
        factory = {
            val mapView = applier.mapView
            val polyline = Polyline(mapView)
            polyline.apply {
                setPoints(geoPoints)
                outlinePaint.color = color.toArgb()
                outlinePaint.strokeWidth = width

                outlinePaint.strokeCap = when (cap) {
                    PolylineCap.BUTT -> Paint.Cap.BUTT
                    PolylineCap.ROUND -> Paint.Cap.ROUND
                    PolylineCap.SQUARE -> Paint.Cap.SQUARE
                }

                isVisible = visible
                id?.let { this.id = id }

                mapView.overlayManager.add(this)
                onPolylineLoaded.invoke(outlinePaint)

                infoWindow = null
            }

            val composeView = ComposeView(context)
                .apply {
                    setContent {
                        infoWindowContent.invoke(InfoWindowData(title.orEmpty(), snippet.orEmpty()))
                    }
                }

            val infoWindow = OsmInfoWindow(composeView, mapView)
            infoWindow.view.setOnClickListener {
                if (infoWindow.isOpen) infoWindow.close()
            }
            polyline.infoWindow = infoWindow

            PolylineNode(mapView, polyline, onClick).also { it.setupListeners() }
        }, update = {
            set(geoPoints) { polyline.setPoints(it) }
            set(color) { polyline.outlinePaint.color = it.toArgb() }

            update(visible) { polyline.isVisible = visible }
        })
}