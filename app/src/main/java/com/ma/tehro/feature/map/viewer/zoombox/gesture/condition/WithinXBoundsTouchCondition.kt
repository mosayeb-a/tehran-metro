package com.ma.tehro.feature.map.viewer.zoombox.gesture.condition

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import com.ma.tehro.feature.map.viewer.zoombox.ZoomState
import com.ma.tehro.feature.map.viewer.zoombox.util.Calculator

class WithinXBoundsTouchCondition : TouchCondition {

    override fun invoke(
        zoomStateProvider: () -> ZoomState,
        pointerInputScope: PointerInputScope,
        pointerEvent: PointerEvent
    ): Boolean {
        val zoomState = zoomStateProvider()

        val translationX = pointerEvent.changes.first().previousPosition.x -
                pointerEvent.changes.first().position.x
        val maxTranslationX = Calculator.calculateMaxTranslation(
            zoomState.scale,
            pointerInputScope.size.width
        )
        zoomState.offset.x

        return pointerEvent.changes.size > 1 ||
                zoomState.offset.x + translationX in 0.0..maxTranslationX.toDouble()
    }
}