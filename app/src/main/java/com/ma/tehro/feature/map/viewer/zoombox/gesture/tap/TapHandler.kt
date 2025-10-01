package com.ma.tehro.feature.map.viewer.zoombox.gesture.tap

import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import com.ma.tehro.feature.map.viewer.zoombox.ZoomState

class TapHandler(
    private val onDoubleTap: OnDoubleTapHandler? = ZoomOnDoubleTapHandler(),
    private val onLongPress: ((Offset) -> Unit)? = null,
    private val onPress: suspend PressGestureScope.(Offset) -> Unit = { },
    private val onTap: (() -> Unit)? = null
) {

    suspend operator fun invoke(
        scope: CoroutineScope,
        pointerInputScope: PointerInputScope,
        state: TransformableState,
        zoomRange: ClosedFloatingPointRange<Float>,
        zoomStateProvider: () -> ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        pointerInputScope.detectTapGestures(
            onDoubleTap = onDoubleTap?.let { onDoubleTap ->
                { offset: Offset ->
                    onDoubleTap.invoke(
                        scope,
                        pointerInputScope,
                        state,
                        zoomRange,
                        offset,
                        zoomStateProvider,
                        onZoomUpdated
                    )
                }
            },
            onLongPress = onLongPress,
            onPress = onPress,
            onTap = onTap?.let { onTap -> { onTap() } }
        )
    }
}