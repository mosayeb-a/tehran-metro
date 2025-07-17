package com.ma.tehro.feature.line.stations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.ma.tehro.R
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.BilingualText
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
                fa = lineName.fa,
                en = lineName.en,
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
                        .clickable { onStationClick(station, lineNumber) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StationItem(
                        modifier = Modifier
                            .weight(1f),
                        station = station,
                        lineNumber = lineNumber,
                    )

                    Spacer(Modifier.width(16.dp))

                    SingleNode(
                        modifier = Modifier.padding(end = 16.dp),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f),
                        nodeType = nodeType,
                        nodeSize = 20f,
                        isChecked = true,
                        lineWidth = .8f
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
    station: Station,
    lineNumber: Int,
    modifier: Modifier = Modifier,
    showTransferIndicator: Boolean = true,
    maxCircleSize: Dp = 36.dp,
    minCircleSize: Dp = 28.dp,
    iconSize: Dp = 18.dp,
    verticalOffset: Dp = (-8).dp,
) {

    val transferLines = remember(station, lineNumber) {
        station.lines.filter { it != lineNumber }
    }
    val colors = remember(transferLines) {
        transferLines.map { getLineColorByNumber(it) }
    }
    val circleSizeStep = remember(colors) {
        if (colors.size > 1) {
            (maxCircleSize - minCircleSize) / (colors.size - 1).coerceAtLeast(1)
        } else {
            0.dp
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        if (showTransferIndicator && colors.isNotEmpty()) {
            TransferIndicator(
                colors = colors,
                maxCircleSize = maxCircleSize,
                circleSizeStep = circleSizeStep,
                iconSize = iconSize,
                verticalOffset = verticalOffset,
                contentDescription = "Transfer lines for ${station.translations.fa}"
            )
        } else {
            Box(modifier = Modifier.size(maxCircleSize))
        }

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            BilingualText(
                fa = station.translations.fa,
                en = station.name.uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End,
                maxLine = 2 
            )
        }
    }
}

@Composable
private fun TransferIndicator(
    colors: List<Color>,
    maxCircleSize: Dp,
    circleSizeStep: Dp,
    iconSize: Dp,
    verticalOffset: Dp,
    contentDescription: String
) {
    Box(
        contentAlignment = Alignment.CenterStart
    ) {
        colors.forEachIndexed { index, color ->
            val circleSize = maxCircleSize - (index * circleSizeStep)
            Box(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(color)
                    .align(Alignment.Center)
                    .offset(y = verticalOffset * index)
            ) {
                Icon(
                    modifier = Modifier
                        .size(iconSize)
                        .align(Alignment.Center),
                    painter = painterResource(R.drawable.sync_alt_24px),
                    contentDescription = contentDescription,
                    tint = Color.White
                )
            }
        }
    }
}
