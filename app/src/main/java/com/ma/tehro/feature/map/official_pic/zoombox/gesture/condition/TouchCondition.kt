package com.ma.tehro.feature.map.official_pic.zoombox.gesture.condition

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import com.ma.tehro.feature.map.official_pic.zoombox.ZoomState

interface TouchCondition {

    operator fun invoke(
        zoomStateProvider: () -> ZoomState,
        pointerInputScope: PointerInputScope,
        pointerEvent: PointerEvent
    ): Boolean
}