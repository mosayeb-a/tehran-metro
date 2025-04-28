package com.ma.tehro.feature.map.official_pic.zoombox.gesture.condition

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import com.ma.tehro.feature.map.official_pic.zoombox.ZoomState

class OnDoubleTouchCondition : TouchCondition {

    override fun invoke(
        zoomStateProvider: () -> ZoomState,
        pointerInputScope: PointerInputScope,
        pointerEvent: PointerEvent
    ) = pointerEvent.changes.size > 1

}
