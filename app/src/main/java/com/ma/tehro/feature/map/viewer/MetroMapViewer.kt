package com.ma.tehro.feature.map.viewer

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size
import coil3.svg.SvgDecoder
import com.ma.tehro.R
import com.ma.tehro.common.SvgStationParser
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.feature.map.viewer.zoombox.MutableZoomState
import com.ma.tehro.feature.map.viewer.zoombox.ZoomState
import com.ma.tehro.feature.map.viewer.zoombox.gesture.condition.WithinXBoundsTouchCondition
import com.ma.tehro.feature.map.viewer.zoombox.gesture.transform.TransformGestureHandler
import com.ma.tehro.feature.map.viewer.zoombox.zoomable

const val svgWidth = 120f
const val svgHeight = 120f

@Composable
fun MetroMapViewer(
    modifier: Modifier = Modifier,
    stations: List<String>?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val zoomState =
        remember { MutableZoomState(ZoomState(scale = 1f, offset = Offset.Zero, childRect = null)) }
    val stationCoordinates = remember(stations) {
        stations?.let { SvgStationParser.parseStations(context, it) } ?: emptyMap()
    }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        )
    )

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
                        .data(R.raw.tehran_map)
                        .decoderFactory(SvgDecoder.Factory(useViewBoundsAsIntrinsicSize = false))
                        .size(Size(3000, 3000))
                        .build(),
                    contentDescription = "metro map pic",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                stations?.let {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val imageSize = size
                        val baseScale =
                            minOf(imageSize.width / svgWidth, imageSize.height / svgHeight)
                        val offsetX = (imageSize.width - svgWidth * baseScale) / 2
                        val offsetY = (imageSize.height - svgHeight * baseScale) / 2

                        for (i in 0 until stations.size - 1) {
                            val station1 = stations[i]
                            val station2 = stations[i + 1]
                            stationCoordinates[station1]?.let { (x1, y1) ->
                                stationCoordinates[station2]?.let { (x2, y2) ->
                                    drawLine(
                                        color = Color.White,
                                        start = Offset(
                                            x1 * baseScale + offsetX,
                                            y1 * baseScale + offsetY
                                        ),
                                        end = Offset(
                                            x2 * baseScale + offsetX,
                                            y2 * baseScale + offsetY
                                        ),
                                        strokeWidth = 0.7f * baseScale,
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }

                        stations.forEachIndexed { index, station ->
                            stationCoordinates[station]?.let { (x, y) ->
                                val center = Offset(
                                    x * baseScale + offsetX,
                                    y * baseScale + offsetY
                                )
                                when (index) {
                                    0 -> {
                                        drawCircle(
                                            color = Color.Green,
                                            radius = 1.0f * baseScale * pulseScale,
                                            center = center
                                        )
                                        drawCircle(
                                            color = Color.White,
                                            radius = 0.5f * baseScale,
                                            center = center
                                        )
                                    }
                                    stations.size - 1 -> {
                                        drawCircle(
                                            color = Color.Magenta,
                                            radius = 1.0f * baseScale * pulseScale,
                                            center = center
                                        )
                                        drawCircle(
                                            color = Color.White,
                                            radius = 0.5f * baseScale,
                                            center = center
                                        )
                                    }
                                    else -> {
                                        drawCircle(
                                            color = Color.White,
                                            radius = 0.7f * baseScale,
                                            center = center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}