package com.ma.tehro.feature.map.official_pic.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import com.ma.tehro.feature.map.official_pic.zoombox.ZoomState
import com.ma.tehro.feature.map.official_pic.zoombox.gesture.condition.AnyTouchCondition
import com.ma.tehro.feature.map.official_pic.zoombox.gesture.condition.TouchCondition
import com.ma.tehro.feature.map.official_pic.zoombox.util.detectTransformGestures

class TransformGestureHandler(
    private val onCancelledBehavior: OnCancelledBehavior = KeepWithinBoundsOnCancelledBehavior(),
    private val onCondition: TouchCondition = AnyTouchCondition(),
    private val onPinchGesture: OnPinchGestureHandler = OnPinchToZoomGestureHandler()
) {

    suspend operator fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        zoomStateProvider: () -> ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        pointerInputScope.detectTransformGestures(
            zoomStateProvider,
            pointerInputScope,
            onCancelled = {
                onCancelledBehavior(
                    scope,
                    state,
                    pointerInputScope,
                    zoomStateProvider(),
                    onZoomUpdated
                )
            },
            onCondition = onCondition
        ) { centroid: Offset, pan: Offset, gestureZoom: Float ->
            onPinchGesture(
                centroid,
                zoomRange,
                pan,
                zoomStateProvider(),
                gestureZoom,
                onZoomUpdated
            )
        }
    }
}
