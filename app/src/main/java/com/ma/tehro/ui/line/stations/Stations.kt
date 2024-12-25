package com.ma.tehro.ui.line.stations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.getLineColor
import com.ma.tehro.common.getLineEndpoints
import com.ma.tehro.common.timelineview.TimelineView
import com.ma.tehro.common.timelineview.TimelineView.SingleNode
import com.ma.tehro.data.StationData

@Composable
fun Stations(
    lineNumber: Int,
    orderedStations: List<StationData>,
    onBackClick: () -> Unit = {},
) {
    val lineColor = remember { getLineColor(lineNumber) }
    val endpoint = remember { getLineEndpoints() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Appbar(
                title = "Line $lineNumber - ${endpoint[lineNumber]?.first}/${endpoint[lineNumber]?.second}",
                handleBack = true,
                onBackClick = onBackClick
            )
        }) {
        StationList(
            modifier = Modifier.padding(it),
            stations = orderedStations,
            lineColor = lineColor
        )
    }
}

@Composable
fun StationList(
    modifier: Modifier = Modifier,
    stations: List<StationData>,
    lineColor: Color
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val itemHeight =
        ((screenHeight / (stations.size + 1).coerceAtLeast(1)) * 2.4f)
            .coerceAtLeast(72f)
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        itemsIndexed(
            stations,
            key = { _, station -> station.property.name }) { index, station ->

            val nodeType = when (index) {
                0 -> TimelineView.NodeType.FIRST
                stations.size - 1 -> TimelineView.NodeType.LAST
                else -> TimelineView.NodeType.MIDDLE
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight.dp)
                    .background(lineColor)
                    .clickable { }
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
                    itemHeight = itemHeight,
                    lineColor = lineColor,
                )
            }

            if (index < stations.size - 1) {
                HorizontalDivider(thickness = .28.dp)
            }
        }
    }
}

@Composable
fun StationItem(
    modifier: Modifier = Modifier,
    station: StationData,
    itemHeight: Float,
    lineColor: Color
) {
    val lineColorString = "#${Integer.toHexString(lineColor.toArgb()).uppercase()}"

    val normalizedLineColorString = lineColorString.takeLast(6)
    val colors: List<String> = station.property.colors
        .map { it.takeLast(6) }
        .filter { it != normalizedLineColorString }

    val maxCircleSize = 36.dp
    val minCircleSize = 28.dp
    val circleSizeStep = (maxCircleSize - minCircleSize) / (colors.size - 1).coerceAtLeast(1)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = station.property.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )


        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd
        ) {
            colors.forEachIndexed { index, colorString ->
                val circleSize = maxCircleSize - (index * circleSizeStep)

                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor("#$colorString")))
                        .align(Alignment.Center)
                        .offset(y = (index * -8).dp)
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(R.drawable.sync_alt_24px),
                        contentDescription = "See stations by line",
                        tint = Color.White
                    )
                }
            }
        }
    }
}