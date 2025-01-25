package com.ma.tehro.ui.shortestpath.pathfinder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.R
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.timelineview.TimelineView
import com.ma.tehro.common.timelineview.TimelineView.SingleNode
import com.ma.tehro.common.toImageBitmap
import com.ma.tehro.data.Station
import com.ma.tehro.ui.detail.repo.PathItem
import com.ma.tehro.ui.line.stations.StationItem

@Composable
fun PathFinder(
    modifier: Modifier = Modifier,
    fromEn: String,
    fromFa: String,
    toEn: String,
    toFa: String,
    findShortestPath: () -> List<PathItem>,
    onBack: () -> Unit,
    onStationClick: (station: Station, lineNumber: Int) -> Unit
) {
    var path by remember { mutableStateOf<List<PathItem>>(emptyList()) }

    LaunchedEffect(fromEn, toEn) {
        if (path.isEmpty()) {
            path = findShortestPath()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            Appbar(fromEn = fromEn, toEn = toEn, onBack = onBack, fromFa = fromFa, toFa = toFa,)
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier.padding(padding),
        ) {
            itemsIndexed(
                items = path,
                key = { index, _ -> index }
            ) { index, item ->
                when (item) {
                    is PathItem.Title -> {
                        PinableTitle(
                            en = item.en,
                            fa = item.fa,
                            isFirstItem = index == 0,
                            lineNumber = item.en[5].digitToInt()
                        )
                    }

                    is PathItem.StationItem -> {
                        StationRow(
                            modifier = Modifier.clickable {
                                onStationClick(item.station, item.lineNumber)
                            },
                            station = item.station,
                            itemHeight = 76f,
                            isLastItem = index == path.size - 1,
                            disabled = item.isPassthrough,
                            lineNumber = item.lineNumber,

                            )
                        HorizontalDivider(thickness = .28.dp)
                    }

                }
            }
        }
    }
}

@Composable
fun PinableTitle(
    modifier: Modifier = Modifier,
    fa: String,
    en: String,
    isFirstItem: Boolean,
    lineNumber: Int,
) {
    val iconPainter =
        painterResource(
            id = if (isFirstItem) R.drawable.arrow_drop_down_24px else R.drawable.sync_alt_24px)

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val iconImageBitmap = remember {
        iconPainter.toImageBitmap(
            size = Size(32f, 32f),
            density = density,
            layoutDirection = layoutDirection
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(MaterialTheme.colorScheme.secondary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SingleNode(
            modifier = Modifier.padding(start = 12.dp),
            color = getLineColorByNumber(lineNumber),
            nodeType = if (isFirstItem) TimelineView.NodeType.FIRST else TimelineView.NodeType.MIDDLE,
            nodeSize = 36f,
            isChecked = true,
            lineWidth = 0.8f,
            iconBitmap = if (isFirstItem) iconImageBitmap else iconImageBitmap
        )
        Text(
            text = en,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            textAlign = TextAlign.Start
        )
        Text(
            text = fa,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
            textAlign = TextAlign.End,

            )
    }
}

@Composable
fun StationRow(
    modifier: Modifier = Modifier,
    station: Station,
    itemHeight: Float,
    isLastItem: Boolean,
    disabled: Boolean = false,
    lineNumber: Int
) {
    val color = getLineColorByNumber(lineNumber)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight.dp)
            .alpha(if (disabled) 0.7f else 1f)
            .background(color)

    ) {
        SingleNode(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            nodeType = if (disabled) {
                TimelineView.NodeType.SPACER
            } else {
                if (isLastItem) TimelineView.NodeType.LAST else TimelineView.NodeType.MIDDLE
            },
            nodeSize = 20f,
            isChecked = !disabled,
            lineWidth = 0.8f
        )

        StationItem(
            modifier = Modifier.weight(1f),
            station = station,
            itemHeight = itemHeight,
            lineNumber = lineNumber
        )

        if (disabled) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ایستگاه غیرفعال است\n" +
                            "The station is inactive",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}