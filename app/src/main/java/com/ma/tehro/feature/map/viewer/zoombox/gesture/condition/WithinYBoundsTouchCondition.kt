package com.ma.tehro.feature.map.viewer.zoombox.gesture.condition

import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import com.ma.tehro.feature.map.viewer.zoombox.ZoomState
import com.ma.tehro.feature.map.viewer.zoombox.util.Calculator

class WithinYBoundsTouchCondition : TouchCondition {

    override fun invoke(
        zoomStateProvider: () -> ZoomState,
        pointerInputScope: PointerInputScope,
        pointerEvent: PointerEvent
    ): Boolean {
        val zoomState = zoomStateProvider()

        val translationY = pointerEvent.changes.first().previousPosition.y -
                pointerEvent.changes.first().position.y
        val maxTranslationY = Calculator.calculateMaxTranslation(
            zoomState.scale,
            pointerInputScope.size.height
        )
        zoomState.offset.y

        return pointerEvent.changes.size > 1 ||
                zoomState.offset.y + translationY in 0.0..maxTranslationY.toDouble()
    }
}