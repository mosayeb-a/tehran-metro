package com.ma.tehro.feature.map.viewer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.onSizeChanged
import com.ma.tehro.common.STATION_COORDS_QUALIFIER
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.domain.map.MapStationCoordinate
import com.ma.tehro.domain.map.PathPoint
import com.ma.tehro.feature.map.viewer.zoombox.MutableZoomState
import com.ma.tehro.feature.map.viewer.zoombox.ZoomState
import com.ma.tehro.feature.map.viewer.zoombox.gesture.condition.WithinXBoundsTouchCondition
import com.ma.tehro.feature.map.viewer.zoombox.gesture.transform.TransformGestureHandler
import com.ma.tehro.feature.map.viewer.zoombox.zoomable
import com.ma.thero.resources.Res
import com.ma.thero.resources.map
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

private const val SVG_WIDTH = 1984.25f
private const val SVG_HEIGHT = 1417.32f
private const val CIRCLE_RADIUS = 3.5f
private const val LINE_WIDTH = 3.1f
private val START_COLOR = Color(0xFF00FF82)
private val DEST_COLOR = Color(0xFF00FFDE)

// fake points guide the path along metro lines when stations aren't directly connected
// key is a pair (from, to) in the direction the fake points are defined
private val fakePoints = mapOf(
    "Namayeshgah-e Shahr-e Aftab" to "Emam Khomeini Airport" to listOf(
        PathPoint.Fake(1122, 1300)
    ),
    "Towhid" to "Modafean-e Salamat" to listOf(
        PathPoint.Fake(968, 598),
        PathPoint.Fake(917, 547)
    )
)

@Composable
fun MapViewer(
    modifier: Modifier = Modifier,
    stations: List<String>?,
    onBack: () -> Unit
) {
    val stationCoords: Map<String, MapStationCoordinate> =
        koinInject(qualifier = named(STATION_COORDS_QUALIFIER))

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val zoomState =
        remember { MutableZoomState(ZoomState(scale = 1f, offset = Offset.Zero, childRect = null)) }

    val pathPoints = remember(stations) {
        if (stations == null) return@remember emptyList()

        val result = mutableListOf<PathPoint>()
        for (i in 0 until stations.size - 1) {
            val current = stations[i]
            val next = stations[i + 1]
            result.add(PathPoint.Real(current))

            val fake = fakePoints[current to next] ?: fakePoints[next to current]
            if (fake != null) {
                val orderedFake = if (fakePoints.containsKey(current to next)) {
                    fake
                } else {
                    fake.reversed()
                }
                result.addAll(orderedFake)
            }
        }
        if (stations.isNotEmpty()) {
            result.add(PathPoint.Real(stations.last()))
        }
        result
    }

    val scaledPoints = remember(pathPoints, stationCoords, canvasSize) {
        if (canvasSize.width == 0f || canvasSize.height == 0f) return@remember emptyList()

        val scale = minOf(canvasSize.width / SVG_WIDTH, canvasSize.height / SVG_HEIGHT)
        val offsetX = (canvasSize.width - SVG_WIDTH * scale) / 2
        val offsetY = (canvasSize.height - SVG_HEIGHT * scale) / 2

        pathPoints.mapNotNull { point ->
            val coord = when (point) {
                is PathPoint.Real -> stationCoords[point.name]
                is PathPoint.Fake -> MapStationCoordinate(point.x, point.y)
            }
            coord?.let { c ->
                Offset(
                    c.x.toFloat() * scale + offsetX,
                    c.y.toFloat() * scale + offsetY
                )
            }
        }
    }

    val isRealStation = remember(pathPoints) {
        pathPoints.map { it is PathPoint.Real }
    }

    val pulse by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
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
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { size ->
                            canvasSize = Size(
                                width = size.width.toFloat(),
                                height = size.height.toFloat()
                            )
                        },
                    painter = painterResource(Res.drawable.map),
                    contentDescription = null
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (scaledPoints.size >= 2) {
                        val path = androidx.compose.ui.graphics.Path()
                        path.moveTo(scaledPoints.first().x, scaledPoints.first().y)

                        for (i in 1 until scaledPoints.size) {
                            val prev = scaledPoints[i - 1]
                            val curr = scaledPoints[i]

                            val cp1x = prev.x + (curr.x - prev.x) * 0.3f
                            val cp1y = prev.y + (curr.y - prev.y) * 0.3f
                            val cp2x = curr.x - (curr.x - prev.x) * 0.3f
                            val cp2y = curr.y - (curr.y - prev.y) * 0.3f

                            path.cubicTo(cp1x, cp1y, cp2x, cp2y, curr.x, curr.y)
                        }

                        drawPath(
                            path = path,
                            color = White,
                            style = Stroke(width = LINE_WIDTH)
                        )
                    }

                    scaledPoints.forEachIndexed { index, point ->
                        if (isRealStation[index]) {
                            when (index) {
                                0 -> {
                                    drawCircle(
                                        color = START_COLOR.copy(alpha = 0.3f),
                                        radius = (CIRCLE_RADIUS + 4.2f) * pulse,
                                        center = point
                                    )
                                    drawCircle(
                                        color = START_COLOR,
                                        radius = (CIRCLE_RADIUS + 2f) * pulse,
                                        center = point
                                    )
                                    drawCircle(
                                        color = White,
                                        radius = CIRCLE_RADIUS,
                                        center = point
                                    )
                                }

                                scaledPoints.size - 1 -> {
                                    drawCircle(
                                        color = DEST_COLOR.copy(alpha = 0.3f),
                                        radius = (CIRCLE_RADIUS + 4.2f) * pulse,
                                        center = point
                                    )
                                    drawCircle(
                                        color = DEST_COLOR,
                                        radius = (CIRCLE_RADIUS + 2f) * pulse,
                                        center = point
                                    )
                                    drawCircle(
                                        color = White,
                                        radius = CIRCLE_RADIUS,
                                        center = point
                                    )
                                }
                                else -> {
                                    drawCircle(
                                        color = White,
                                        radius = CIRCLE_RADIUS,
                                        center = point
                                    )
                                    drawCircle(
                                        color = Color(0xFF044434),
                                        radius = CIRCLE_RADIUS / 2f,
                                        center = point
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