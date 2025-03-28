package com.ma.tehro.ui.line.stations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.calculateLineName
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.timelineview.TimelineView
import com.ma.tehro.common.timelineview.TimelineView.SingleNode
import com.ma.tehro.data.Station

@Composable
fun Stations(
    lineNumber: Int,
    useBranch: Boolean,
    orderedStations: List<Station>,
    onBackClick: () -> Unit,
    onStationClick: (station: Station, lineNumber: Int) -> Unit
) {
    val lineColor = remember { getLineColorByNumber(lineNumber) }
    val lineName = remember(lineNumber) { calculateLineName(lineNumber, useBranch) }

    Scaffold(
        topBar = {
            Appbar(
                title = lineName,
                handleBack = true,
                onBackClick = onBackClick
            )
        }) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            itemsIndexed(
                orderedStations,
                key = { _, station -> station.name }
            ) { index, station ->
                val nodeType = when (index) {
                    0 -> TimelineView.NodeType.FIRST
                    orderedStations.size - 1 -> TimelineView.NodeType.LAST
                    else -> TimelineView.NodeType.MIDDLE
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp)
                        .background(lineColor)
                        .clickable { onStationClick(station, lineNumber) }
                ) {
                    SingleNode(
                        modifier = Modifier.padding(start = 16.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f),
                        nodeType = nodeType,
                        nodeSize = 20f,
                        isChecked = true,
                        lineWidth = .8f
                    )
                    StationItem(
                        modifier = Modifier
                            .weight(1f),
                        station = station,
                        lineNumber = lineNumber,
                    )
                }

                if (index < orderedStations.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.77.dp)
                            .background(lineColor.copy(alpha = 0.9f))
                    )
                }
            }
        }
    }
}

@Composable
fun StationItem(
    modifier: Modifier = Modifier,
    station: Station,
    lineNumber: Int
) {
    val colors = station.lines
        .filter { it != lineNumber }
        .map { getLineColorByNumber(it) }

    val maxCircleSize = 36.dp
    val minCircleSize = 28.dp
    val circleSizeStep = (maxCircleSize - minCircleSize) / (colors.size - 1)
        .coerceAtLeast(1)

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = station.translations.fa,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .padding(start = 8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            colors.forEachIndexed { index, color ->
                val circleSize = maxCircleSize - (index * circleSizeStep)
                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .clip(CircleShape)
                        .background(color)
                        .align(Alignment.Center)
                        .offset(y = (index * -8).dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.Center),
                        painter = painterResource(R.drawable.sync_alt_24px),
                        contentDescription = "See stations by line",
                        tint = Color.White
                    )
                }
            }
        }
    }
}