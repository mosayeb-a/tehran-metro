package com.ma.tehro.feature.map.viewer.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.ma.tehro.feature.map.viewer.zoombox.ZoomState
import com.ma.tehro.feature.map.viewer.zoombox.util.animateZoomBy

class ResetToOriginalPositionOnCancelledBehavior : OnCancelledBehavior {

    override fun invoke(
        scope: CoroutineScope,
        state: TransformableState,
        pointerInputScope: PointerInputScope,
        zoomState: ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        scope.launch {
            state.animateZoomBy(
                zoomState,
                ZoomState(),
                onZoomUpdated = onZoomUpdated
            )
        }
    }
}