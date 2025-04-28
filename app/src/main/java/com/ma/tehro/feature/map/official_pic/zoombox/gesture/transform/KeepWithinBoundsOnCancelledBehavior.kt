package com.ma.tehro.feature.map.official_pic.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.ma.tehro.feature.map.official_pic.zoombox.ZoomState
import com.ma.tehro.feature.map.official_pic.zoombox.util.Calculator
import com.ma.tehro.feature.map.official_pic.zoombox.util.animateZoomBy

class KeepWithinBoundsOnCancelledBehavior : OnCancelledBehavior {

    override fun invoke(
        scope: CoroutineScope,
        state: TransformableState,
        pointerInputScope: PointerInputScope,
        zoomState: ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        val maxTranslationX = Calculator.calculateMaxTranslation(
            zoomState.scale,
            pointerInputScope.size.width
        )
        val maxTranslationY = Calculator.calculateMaxTranslation(
            zoomState.scale,
            pointerInputScope.size.height
        )

        val isWithinXBounds = zoomState.offset.x in 0.0..maxTranslationX.toDouble()
        val isWithinYBounds = zoomState.offset.y in 0.0..maxTranslationY.toDouble()
        if (isWithinXBounds && isWithinYBounds) return

        zoomState.childRect ?: return

        scope.launch {
            val translationXWithinBounds = Calculator.keepTranslationWithinBounds(
                -zoomState.offset.x,
                zoomState.scale,
                pointerInputScope.size.width,
                zoomState.childRect.width.toInt()
            )
            val translationYWithinBounds = Calculator.keepTranslationWithinBounds(
                -zoomState.offset.y,
                zoomState.scale,
                pointerInputScope.size.height,
                zoomState.childRect.height.toInt()
            )

            state.animateZoomBy(
                zoomState,
                zoomState.copy(
                    offset = Offset(
                        translationXWithinBounds,
                        translationYWithinBounds
                    )
                ),
                onZoomUpdated = onZoomUpdated
            )
        }
    }
}