package com.ma.tehro.common.timelineview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.timelineview.SingleNodeDrawings.drawBottomLine
import com.ma.tehro.common.timelineview.SingleNodeDrawings.drawNodeCircle
import com.ma.tehro.common.timelineview.SingleNodeDrawings.drawSpacerLine
import com.ma.tehro.common.timelineview.SingleNodeDrawings.drawTopLine

object TimelineView {
    enum class NodeType {
        FIRST,
        MIDDLE,
        LAST,
        SPACER,
    }

    @Composable
    fun SingleNode(
        color: Color,
        nodeType: NodeType,
        nodeSize: Float,
        modifier: Modifier = Modifier,
        isChecked: Boolean = false,
        isDashed: Boolean = false,
        lineWidth: Float = (nodeSize / 4).coerceAtMost(40f),
        iconBitmap: ImageBitmap? = null
    ) {
        Canvas(
            modifier = modifier
                .fillMaxHeight()
                .width((nodeSize / 2).dp)
        ) {
            val nodeRadius = nodeSize / 2

            when (nodeType) {
                NodeType.FIRST -> {
                    drawNodeCircle(isChecked, color, nodeRadius)
                    drawBottomLine(isDashed, color, lineWidth, nodeRadius)
                }

                NodeType.MIDDLE -> {
                    drawTopLine(isDashed, color, lineWidth, nodeRadius)
                    drawNodeCircle(isChecked, color, nodeRadius)
                    drawBottomLine(isDashed, color, lineWidth, nodeRadius)
                }

                NodeType.LAST -> {
                    drawTopLine(isDashed, color, lineWidth, nodeRadius)
                    drawNodeCircle(isChecked, color, nodeRadius)
                }

                NodeType.SPACER -> {
                    drawSpacerLine(isDashed, color, lineWidth)
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
                    style = Fill
                )
            }
        }
    }

}