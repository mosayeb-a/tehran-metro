package com.ma.tehro.feature.shortestpath.pathfinder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.timelineview.TimelineView
import com.ma.tehro.common.timelineview.TimelineView.SingleNode
import com.ma.tehro.common.toImageBitmap

@Composable
fun PinableTitle(
    modifier: Modifier = Modifier,
    fa: String,
    en: String,
    isFirstItem: Boolean,
    lineNumber: Int,
) {
    val icon = remember(isFirstItem) {
        if (isFirstItem) Icons.Rounded.ArrowDropDown else Icons.Rounded.SyncAlt
    }

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val iconPainter = rememberVectorPainter(icon)
    val iconImageBitmap = remember(iconPainter, density, layoutDirection) {
        iconPainter.toImageBitmap(size = Size(32f, 32f), density, layoutDirection)
    }
    val lineColor = remember(lineNumber) {
        getLineColorByNumber(lineNumber)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 12.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = en.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .92f),
            fontSize = 11.sp,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            textAlign = TextAlign.Start
        )
        Text(
            text = fa,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .92f),
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            textAlign = TextAlign.End,
        )
        Spacer(Modifier.width(6.dp))
        SingleNode(
            modifier = Modifier,
            nodeColor = lineColor,
            nodeType = if (isFirstItem) TimelineView.NodeType.FIRST else TimelineView.NodeType.MIDDLE,
            nodeSize = 36f,
            isChecked = true,
            lineWidth = 0.8f,
            iconBitmap = iconImageBitmap,
        )
    }
}