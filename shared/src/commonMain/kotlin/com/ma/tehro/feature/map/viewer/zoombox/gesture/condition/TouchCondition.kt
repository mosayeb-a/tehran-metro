package com.ma.tehro.feature.map.viewer.zoombox.gesture.condition

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import com.ma.tehro.feature.map.viewer.zoombox.ZoomState

interface TouchCondition {

    operator fun invoke(
        zoomStateProvider: () -> ZoomState,
        pointerInputScope: PointerInputScope,
        pointerEvent: PointerEvent
    ): Boolean
}