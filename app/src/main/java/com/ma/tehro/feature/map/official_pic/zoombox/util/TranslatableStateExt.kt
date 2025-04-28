package com.ma.tehro.feature.map.official_pic.zoombox.util

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.ma.tehro.feature.map.official_pic.zoombox.ZoomState

suspend fun TransformableState.animateZoomBy(
    previousZoomState: ZoomState,
    scale: Float,
    touchPoint: Offset,
    size: IntSize,
    childImageBounds: Rect,
    zoomAnimationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessMediumLow),
    onZoomUpdated: (ZoomState) -> Unit
) {
    require(scale > 0) {
        "scale value should be greater than 0"
    }
    val translationX = Calculator.calculateFutureTranslation(
        scale,
        touchPoint.x,
        size.width,
        childImageBounds.width.toInt()
    )
    val translationY = Calculator.calculateFutureTranslation(
        scale,
        touchPoint.y,
        size.height,
        childImageBounds.height.toInt()
    )

    animateZoomBy(
        previousZoomState,
        previousZoomState.copy(
            scale = scale,
            offset = Offset(
                translationX,
                translationY
            )
        ), zoomAnimationSpec, onZoomUpdated
    )
}

suspend fun TransformableState.animateZoomBy(
    previousZoomState: ZoomState,
    nextZoomState: ZoomState,
    zoomAnimationSpec: AnimationSpec<Float> = SpringSpec(stiffness = Spring.StiffnessLow),
    onZoomUpdated: (ZoomState) -> Unit
) {
    var currentZoom = previousZoomState
    transform {
        AnimationState(initialValue = 0f).animateTo(1f, zoomAnimationSpec) {
            val newScale = previousZoomState.scale + this.value * (nextZoomState.scale - previousZoomState.scale)

            currentZoom = currentZoom.copy(
                scale = newScale,
                offset = Offset(
                    x = previousZoomState.offset.x * (1 - this.value) - nextZoomState.offset.x * this.value,
                    y = previousZoomState.offset.y * (1 - this.value) - nextZoomState.offset.y * this.value
                )
            )
            onZoomUpdated(currentZoom)
        }
    }
}