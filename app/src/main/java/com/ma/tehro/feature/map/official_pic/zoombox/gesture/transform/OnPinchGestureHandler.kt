package com.ma.tehro.feature.map.official_pic.zoombox.gesture.transform

import androidx.compose.ui.geometry.Offset
import com.ma.tehro.feature.map.official_pic.zoombox.ZoomState

interface OnPinchGestureHandler {
    operator fun invoke(
        // The position in pixels of the centre zoom position where 0,0 is the top left corner
        centroid: Offset,
        zoomRange: ClosedFloatingPointRange<Float>,
        pan: Offset,
        zoomState: ZoomState,
        gestureZoom: Float,
        onZoomUpdated: (ZoomState) -> Unit
    )
}