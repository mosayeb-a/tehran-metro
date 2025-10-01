package com.ma.tehro.feature.map.viewer.zoombox.gesture.tap

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import com.ma.tehro.feature.map.viewer.zoombox.ZoomState

interface OnDoubleTapHandler {

    operator fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        offset: Offset,
        zoomStateProvider: () -> ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    )
}