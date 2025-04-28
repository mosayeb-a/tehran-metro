package com.ma.tehro.feature.map.official_pic.zoombox.gesture.transform

import androidx.compose.ui.geometry.Offset
import com.ma.tehro.feature.map.official_pic.zoombox.ZoomState
import com.ma.tehro.feature.map.official_pic.zoombox.util.minMax

class OnPinchToZoomGestureHandler : OnPinchGestureHandler {

    override fun invoke(
        // The position in pixels of the centre zoom position where 0,0 is the top left corner
        centroid: Offset,
        zoomRange: ClosedFloatingPointRange<Float>,
        pan: Offset,
        zoomState: ZoomState,
        gestureZoom: Float,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        val newScale = minMax(
            zoomRange.start,
            zoomRange.endInclusive,
            gestureZoom * zoomState.scale
        )
        onZoomUpdated(zoomState.copy(
            scale = newScale,
            offset = Offset(
                zoomState.offset.x + -pan.x * newScale + (newScale - zoomState.scale) * centroid.x,
                zoomState.offset.y + -pan.y * newScale + (newScale - zoomState.scale) * centroid.y,
            )
        ))
    }
}