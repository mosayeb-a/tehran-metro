package com.ma.tehro.feature.map.viewer

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import coil3.svg.SvgDecoder
import com.ma.tehro.domain.MapStationCoordinate
import com.ma.thero.resources.Res
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

private const val SVG_WIDTH = 1984.25f
private const val SVG_HEIGHT = 1417.32f
private const val CIRCLE_RADIUS = 4f
private const val LINE_WIDTH = 4f
private const val ZOOM_MIN = 0.5f
private const val ZOOM_MAX = 5f

@Composable
fun MapViewer(
    modifier: Modifier = Modifier,
    stations: List<String>?,
    onBack: () -> Unit
) {
    val stationCoords: Map<String, MapStationCoordinate> = koinInject(qualifier = named("stationCoords"))
    val context = LocalPlatformContext.current

    var zoom by remember { mutableStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }


    val scaledPoints = remember(stations, stationCoords, canvasSize) {
        if (canvasSize.width == 0f || canvasSize.height == 0f) return@remember emptyList()

        val scale = minOf(canvasSize.width / SVG_WIDTH, canvasSize.height / SVG_HEIGHT)
        val offsetX = (canvasSize.width - SVG_WIDTH * scale) / 2
        val offsetY = (canvasSize.height - SVG_HEIGHT * scale) / 2

        stations?.mapNotNull { name ->
            stationCoords[name]?.let { point ->
                Offset(
                    point.x.toFloat() * scale + offsetX,
                    point.y.toFloat() * scale + offsetY
                )
            }
        } ?: emptyList()
    }
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = panOffset.x
                    translationY = panOffset.y
                    scaleX = zoom
                    scaleY = zoom
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomDelta, _ ->
                        zoom = (zoom * zoomDelta).coerceIn(ZOOM_MIN, ZOOM_MAX)
                        panOffset += pan
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        zoom = 1f
                        panOffset = Offset.Zero
                    })
                }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(Res.getUri("files/map.svg"))
                    .decoderFactory(SvgDecoder.Factory(useViewBoundsAsIntrinsicSize = true))
                    .size(Size(3000, 3000))
                    .crossfade(true)
                    .build(),
                contentDescription = "Metro Map",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { size ->
                        canvasSize = androidx.compose.ui.geometry.Size(
                            width = size.width.toFloat(),
                            height = size.height.toFloat()
                        )
                    }
            )

            androidx.compose.foundation.Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                if (scaledPoints.size >= 2) {
                    val path = androidx.compose.ui.graphics.Path()
                    path.moveTo(scaledPoints.first().x, scaledPoints.first().y)
                    for (i in 1 until scaledPoints.size) {
                        path.lineTo(scaledPoints[i].x, scaledPoints[i].y)
                    }
                    drawPath(
                        path = path,
                        color = Color.Blue,
                        style = Stroke(width = LINE_WIDTH)
                    )
                }

                scaledPoints.forEach { point ->
                    drawCircle(
                        color = Color.Blue.copy(alpha = 0.3f),
                        radius = CIRCLE_RADIUS + 4f,
                        center = point
                    )
                    drawCircle(
                        color = Color.Blue,
                        radius = CIRCLE_RADIUS,
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = CIRCLE_RADIUS / 2.5f,
                        center = point
                    )
                }
            }
        }
    }
}