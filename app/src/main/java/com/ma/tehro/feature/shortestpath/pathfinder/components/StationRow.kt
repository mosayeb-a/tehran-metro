package com.ma.tehro.feature.shortestpath.pathfinder.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.timelineview.TimelineView
import com.ma.tehro.common.timelineview.TimelineView.SingleNode
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.Station
import com.ma.tehro.feature.line.stations.StationItem

@Composable
fun StationRow(
    modifier: Modifier = Modifier,
    station: Station,
    isLastItem: Boolean,
    disabled: Boolean = false,
    lineNumber: Int,
    arrivalTime: String? = null
) {
    val color = getLineColorByNumber(lineNumber)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(78.dp)
            .alpha(if (disabled) 0.93f else 1f)
            .background(color)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 🟠 Arrival Time FIRST (at right side)
        if (arrivalTime != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.Start, // changed to Start
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ساعت ${arrivalTime.toFarsiNumber()}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                )
                Text(
                    text = "AT $arrivalTime",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                )
            }
        }

        // 🟠 Station Name SECOND (in center)
        StationItem(
            modifier = Modifier.weight(2f),
            station = station,
            lineNumber = lineNumber,
            showTransferIndicator = false
        )

        // 🟠 SingleNode LAST (at left side)
        SingleNode(
            color = MaterialTheme.colorScheme.onPrimary,
            nodeType = if (disabled) {
                TimelineView.NodeType.SPACER
            } else {
                if (isLastItem) TimelineView.NodeType.LAST else TimelineView.NodeType.MIDDLE
            },
            nodeSize = 20f,
            isChecked = !disabled,
            lineWidth = 0.8f,
            modifier = Modifier.padding(start = 16.dp) // give a start padding
        )
    }
}

