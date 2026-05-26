package com.ma.tehro.feature.map.viewer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.ma.tehro.common.STATION_COORDS_QUALIFIER
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.domain.MapStationCoordinate
import com.ma.tehro.feature.map.viewer.zoombox.MutableZoomState
import com.ma.tehro.feature.map.viewer.zoombox.ZoomState
import com.ma.tehro.feature.map.viewer.zoombox.gesture.condition.WithinXBoundsTouchCondition
import com.ma.tehro.feature.map.viewer.zoombox.gesture.transform.TransformGestureHandler
import com.ma.tehro.feature.map.viewer.zoombox.zoomable
import com.ma.thero.resources.Res
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

private const val SVG_WIDTH = 1984.25f
private const val SVG_HEIGHT = 1417.32f
private const val CIRCLE_RADIUS = 4f
private const val LINE_WIDTH = 4f

@Composable
fun MapViewer(
    modifier: Modifier = Modifier,
    stations: List<String>?,
    onBack: () -> Unit
) {
    val stationCoords: Map<String, MapStationCoordinate> =
        koinInject(qualifier = named(STATION_COORDS_QUALIFIER))
    val context = LocalPlatformContext.current

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val zoomState =
        remember { MutableZoomState(ZoomState(scale = 1f, offset = Offset.Zero, childRect = null)) }
    var isLoading by remember { mutableStateOf(true) }

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

    Scaffold(
        modifier = modifier,
        topBar = {
            Appbar(
                fa = "نقشه مترو",
                en = "metro map",
                modifier = Modifier.fillMaxWidth(),
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(
                        zoomState = zoomState,
                        zoomRange = 1f..10f,
                        transformGestureHandler = TransformGestureHandler(
                            onCondition = WithinXBoundsTouchCondition()
                        )
                    )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(Res.getUri("files/map.svg"))
                        .decoderFactory(SvgDecoder.Factory(useViewBoundsAsIntrinsicSize = true))
                        .size(coil3.size.Size(4000, 4000))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Metro Map",
                    contentScale = ContentScale.Fit,
                    onSuccess = { isLoading = false },
                    onError = { isLoading = false },
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { size ->
                            canvasSize = Size(
                                width = size.width.toFloat(),
                                height = size.height.toFloat()
                            )
                        }
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = White
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (!isLoading && scaledPoints.size >= 2) {
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

                    if (!isLoading) {
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
                                color = White,
                                radius = CIRCLE_RADIUS / 2.5f,
                                center = point
                            )
                        }
                    }
                }
            }
        }
    }
}