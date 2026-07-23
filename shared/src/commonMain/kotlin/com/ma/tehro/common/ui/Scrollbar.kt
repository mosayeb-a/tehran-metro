package com.ma.tehro.common.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

const val scrollDefaultDelay = 150L
const val scrollBarFadeDuration = 300

fun Modifier.drawVerticalScrollbar(
    state: LazyListState,
    reverseScrolling: Boolean = false
): Modifier = drawScrollbar(state, Orientation.Vertical, reverseScrolling)

private fun Modifier.drawScrollbar(
    state: LazyListState,
    orientation: Orientation,
    reverseScrolling: Boolean
): Modifier = drawScrollbar(
    orientation, reverseScrolling
) { reverseDirection, atEnd, color, alpha ->
    val layoutInfo = state.layoutInfo
    val viewportSize = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
    val totalItemsCount = layoutInfo.totalItemsCount

    if (totalItemsCount > 0) {
        // Get the actual scroll position from the state
        val firstVisibleIndex = state.firstVisibleItemIndex
        val firstVisibleScrollOffset = state.firstVisibleItemScrollOffset.toFloat()

        // Get visible items to calculate average item height
        val visibleItems = layoutInfo.visibleItemsInfo
        val averageItemHeight = if (visibleItems.isNotEmpty()) {
            val totalVisibleHeight = visibleItems.fastSumBy { it.size }
            totalVisibleHeight.toFloat() / visibleItems.size
        } else {
            100f // Default fallback
        }

        // Calculate total content height
        val totalContentHeight = totalItemsCount * averageItemHeight

        // Calculate current scroll position
        val currentScroll = (firstVisibleIndex * averageItemHeight) + firstVisibleScrollOffset
        val maxScroll = (totalContentHeight - viewportSize).coerceAtLeast(1f)

        // Calculate scroll progress (0 to 1)
        val scrollProgress = (currentScroll / maxScroll).coerceIn(0f, 1f)

        val canvasSize = if (orientation == Orientation.Horizontal) size.width else size.height

        // Calculate thumb size
        val thumbSize = if (totalContentHeight > 0) {
            val size = (viewportSize / totalContentHeight * canvasSize)
            size.coerceIn(20f, canvasSize)
        } else {
            canvasSize
        }

        // Calculate start offset based on progress
        val maxStartOffset = canvasSize - thumbSize
        val startOffset = scrollProgress * maxStartOffset

        drawScrollbar(
            orientation, reverseDirection, atEnd, color, alpha, thumbSize, startOffset
        )
    }
}

private fun DrawScope.drawScrollbar(
    orientation: Orientation,
    reverseDirection: Boolean,
    atEnd: Boolean,
    color: Color,
    alpha: () -> Float,
    thumbSize: Float,
    startOffset: Float
) {
    val thicknessPx = Thickness.toPx()
    val topLeft = if (orientation == Orientation.Horizontal) {
        Offset(
            if (reverseDirection) size.width - startOffset - thumbSize else startOffset,
            if (atEnd) size.height - thicknessPx else 0f
        )
    } else {
        Offset(
            if (atEnd) size.width - thicknessPx else 0f,
            if (reverseDirection) size.height - startOffset - thumbSize else startOffset
        )
    }
    val size = if (orientation == Orientation.Horizontal) {
        Size(thumbSize, thicknessPx)
    } else {
        Size(thicknessPx, thumbSize)
    }

    drawRoundRect(
        color = color,
        topLeft = topLeft,
        size = size,
        cornerRadius = CornerRadius(thicknessPx / 2),
        alpha = alpha()
    )
}

private fun Modifier.drawScrollbar(
    orientation: Orientation,
    reverseScrolling: Boolean,
    onDraw: DrawScope.(
        reverseDirection: Boolean,
        atEnd: Boolean,
        color: Color,
        alpha: () -> Float
    ) -> Unit
): Modifier = composed {
    val scrolled = remember {
        MutableSharedFlow<Unit>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    val nestedScrollConnection = remember(orientation, scrolled) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = if (orientation == Orientation.Horizontal) consumed.x else consumed.y
                if (delta != 0f) scrolled.tryEmit(Unit)
                return Offset.Zero
            }
        }
    }

    val alpha = remember { Animatable(0f) }
    LaunchedEffect(scrolled, alpha) {
        scrolled.collectLatest {
            alpha.snapTo(1f)
            delay(scrollDefaultDelay)
            alpha.animateTo(0f, animationSpec = FadeOutAnimationSpec)
        }
    }

    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val reverseDirection = if (orientation == Orientation.Horizontal) {
        if (isLtr) reverseScrolling else !reverseScrolling
    } else reverseScrolling
    val atEnd = if (orientation == Orientation.Vertical) isLtr else true

    val color = BarColor

    Modifier
        .nestedScroll(nestedScrollConnection)
        .drawWithContent {
            drawContent()
            onDraw(reverseDirection, atEnd, color, alpha::value)
        }
}

private val BarColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

private val Thickness = 4.dp
private val FadeOutAnimationSpec =
    tween<Float>(durationMillis = scrollBarFadeDuration)