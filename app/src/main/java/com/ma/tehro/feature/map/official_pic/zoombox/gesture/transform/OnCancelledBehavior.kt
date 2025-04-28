package com.ma.tehro.feature.map.official_pic.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import com.ma.tehro.feature.map.official_pic.zoombox.ZoomState

interface OnCancelledBehavior {

    operator fun invoke(
        scope: CoroutineScope,
        state: TransformableState,
        pointerInputScope: PointerInputScope,
        zoomState: ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    )
}