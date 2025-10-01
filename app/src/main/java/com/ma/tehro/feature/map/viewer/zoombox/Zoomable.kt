package com.ma.tehro.feature.map.viewer.zoombox

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.zIndex
import com.ma.tehro.feature.map.viewer.zoombox.gesture.tap.TapHandler
import com.ma.tehro.feature.map.viewer.zoombox.gesture.transform.TransformGestureHandler

fun Modifier.zoomable(
    zoomRange: ClosedFloatingPointRange<Float> = 1f..3f,
    zoomingZIndex: Float = 1f,
    defaultZIndex: Float = 0f,
    tapHandler: TapHandler = TapHandler(),
    transformGestureHandler: TransformGestureHandler = TransformGestureHandler()
): Modifier = composed {
    then(
        Modifier.zoomable(
            rememberMutableZoomState(),
            zoomRange,
            zoomingZIndex,
            defaultZIndex,
            tapHandler,
            transformGestureHandler
        )
    )
}

fun Modifier.zoomable(
    zoomState: MutableZoomState,
    zoomRange: ClosedFloatingPointRange<Float> = 1f..3f,
    zoomingZIndex: Float = 1f,
    defaultZIndex: Float = 0f,
    tapHandler: TapHandler = TapHandler(),
    transformGestureHandler: TransformGestureHandler = TransformGestureHandler()
): Modifier = composed {
    val scope = rememberCoroutineScope()
    val immutableZoomState = zoomState.value
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        zoomState.value = immutableZoomState.copy(
            scale = immutableZoomState.scale * zoomChange,
            offset = immutableZoomState.offset + offsetChange
        )
    }

    val isMoving = immutableZoomState.scale != 1.0f ||
            immutableZoomState.offset != Offset(0f, 0f)

    val modifier = Modifier
        .graphicsLayer {
            scaleX = immutableZoomState.scale
            scaleY = immutableZoomState.scale
            translationX = -immutableZoomState.offset.x
            translationY = -immutableZoomState.offset.y
            transformOrigin = TransformOrigin(0f, 0f)
        }
        .zIndex(if (isMoving) zoomingZIndex else defaultZIndex)
        .pointerInput(Unit) {
            tapHandler(
                scope,
                this,
                state,
                zoomRange,
                zoomStateProvider = { zoomState.value }
            ) { newZoom ->
                zoomState.value = newZoom
            }
        }
        .pointerInput(Unit) {
            transformGestureHandler.invoke(
                scope,
                this,
                state,
                zoomRange,
                zoomStateProvider = { zoomState.value }
            ) { newZoom ->
                zoomState.value = newZoom
            }
        }
        .then(
            if (immutableZoomState.childRect == null) {
                Modifier.onGloballyPositioned { layoutCoordinates ->
                    val positionInParent = layoutCoordinates.positionInParent()
                    val childRect = Rect(
                        positionInParent.x,
                        positionInParent.y,
                        positionInParent.x + layoutCoordinates.size.width,
                        positionInParent.y + layoutCoordinates.size.height
                    )
                    zoomState.value = immutableZoomState.copy(
                        childRect = childRect
                    )
                }
            } else Modifier
        )
    then(modifier)
}