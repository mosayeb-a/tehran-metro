package com.ma.tehro.feature.map.viewer.zoombox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

@Stable
class MutableZoomState(initial: ZoomState) {

    var value: ZoomState by mutableStateOf(initial, structuralEqualityPolicy())
}

data class ZoomState(
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    internal val childRect: Rect? = null
)

@Composable
fun rememberMutableZoomState(initial: ZoomState = ZoomState()): MutableZoomState {
    val state by remember { mutableStateOf(MutableZoomState(initial)) }
    return state
}