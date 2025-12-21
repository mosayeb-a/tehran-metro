package com.ma.tehro.common.ui.timelineview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.timelineview.SingleNodeDrawings.drawBottomLine
import com.ma.tehro.common.ui.timelineview.SingleNodeDrawings.drawNodeCircle
import com.ma.tehro.common.ui.timelineview.SingleNodeDrawings.drawSpacerLine
import com.ma.tehro.common.ui.timelineview.SingleNodeDrawings.drawTopLine

object TimelineView {
    enum class NodeType {
        FIRST,
        MIDDLE,
        LAST,
        SPACER,
    }

    @Composable
    fun SingleNode(
        nodeType: NodeType,
        nodeSize: Float,
        modifier: Modifier = Modifier,
        isChecked: Boolean = false,
        isDashed: Boolean = false,
        lineWidth: Float = (nodeSize / 4).coerceAtMost(40f),
        iconBitmap: ImageBitmap? = null,
        scale: Float = 1f,
        lineColor: Color = Color.White.copy(alpha = .3f),
        nodeColor: Color = Color.White.copy(alpha = .9f),
        iconTint: Color = Color.White
    ) {
        Canvas(
            modifier = modifier
                .fillMaxHeight()
                .width((nodeSize / 2).dp)
        ) {
            val nodeRadius = nodeSize / 2

            scale(scale, pivot = Offset(size.width / 2, size.height / 2)) {
                when (nodeType) {
                    NodeType.FIRST -> {
                        drawNodeCircle(isChecked, nodeColor, nodeRadius)
                        drawBottomLine(isDashed, lineColor, lineWidth, nodeRadius)
                    }

                    NodeType.MIDDLE -> {
                        drawTopLine(isDashed, lineColor, lineWidth, nodeRadius)
                        drawNodeCircle(isChecked, nodeColor, nodeRadius)
                        drawBottomLine(isDashed, lineColor, lineWidth, nodeRadius)
                    }

                    NodeType.LAST -> {
                        drawTopLine(isDashed, lineColor, lineWidth, nodeRadius)
                        drawNodeCircle(isChecked, nodeColor, nodeRadius)
                    }

                    NodeType.SPACER -> {
                        drawSpacerLine(isDashed, lineColor, lineWidth)
                    }
                }

                if (iconBitmap != null) {
                    val iconSize = nodeRadius * 1.8f
                    val topLeft = Offset(
                        size.width / 2 - iconSize / 2,
                        size.height / 2 - iconSize / 2
                    )
                    drawImage(
                        image = iconBitmap,
                        topLeft = topLeft,
                        alpha = 1f,
                        style = Fill,
                        colorFilter = ColorFilter.tint(iconTint, BlendMode.SrcIn)
                    )
                }
            }
        }
    }
}