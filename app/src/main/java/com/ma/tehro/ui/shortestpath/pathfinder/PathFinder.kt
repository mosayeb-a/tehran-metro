package com.ma.tehro.ui.shortestpath.pathfinder

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ma.tehro.R
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.timelineview.TimelineView
import com.ma.tehro.common.timelineview.TimelineView.SingleNode
import com.ma.tehro.common.toImageBitmap
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.PathItem
import com.ma.tehro.ui.line.stations.StationItem

@Composable
fun PathFinder(
    modifier: Modifier = Modifier,
    fromEn: String,
    fromFa: String,
    toEn: String,
    toFa: String,
    state: PathFinderState,
    onBack: () -> Unit,
    onStationClick: (station: Station, lineNumber: Int) -> Unit,
) {
    val titleIndices = remember(state.shortestPath) {
        state.shortestPath.mapIndexedNotNull { index, item ->
            if (item is PathItem.Title) index to item else null
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            Column {
                Appbar(fromEn = fromEn, toEn = toEn, onBack = onBack, fromFa = fromFa, toFa = toFa)
                EstimatedTimeDisplay(state)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            val lazyListState = rememberLazyListState()

            val currentTitle by remember(lazyListState) {
                derivedStateOf {
                    val firstVisibleItem = lazyListState.firstVisibleItemIndex
                    titleIndices.lastOrNull { (index, _) ->
                        index <= firstVisibleItem
                    }?.second
                }
            }
            LazyColumn(
                contentPadding = padding,
                state = lazyListState
            ) {
                itemsIndexed(
                    items = state.shortestPath,
                    key = { index, item ->
                        when (item) {
                            is PathItem.Title -> "${item.en}_$index"
                            is PathItem.StationItem -> "${item.station.name}_$index"
                        }
                    }
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
                            Column {
                                StationRow(
                                    modifier = Modifier.clickable {
                                        onStationClick(item.station, item.lineNumber)
                                    },
                                    station = item.station,
                                    isLastItem = index == state.shortestPath.size - 1,
                                    disabled = item.isPassthrough,
                                    lineNumber = item.lineNumber,
                                    arrivalTime = state.stationTimes[item.station.name] // Pass the arrival time
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(0.77.dp)
                                        .background(getLineColorByNumber(item.lineNumber).copy(alpha = 0.9f))
                                )
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = currentTitle,
                    transitionSpec = {
                        (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> height } + fadeOut()
                        ).using(
                            SizeTransform(clip = false)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .offset(y = padding.calculateTopPadding())
                        .zIndex(1f), label = ""
                ) { title ->
                    title?.let {
                        PinableTitle(
                            modifier = Modifier.fillMaxWidth(),
                            en = title.en,
                            fa = title.fa,
                            isFirstItem = false,
                            lineNumber = title.en[5].digitToInt()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstimatedTimeDisplay(state: PathFinderState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Estimated travel time: ${state.estimatedTime} minutes",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

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
    val iconRes = remember(isFirstItem) {
        if (isFirstItem) R.drawable.arrow_drop_down_24px else R.drawable.sync_alt_24px
    }
    val iconPainter = painterResource(id = iconRes)

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val iconImageBitmap = remember(iconRes, density, layoutDirection) {
        iconPainter.toImageBitmap(
            size = Size(32f, 32f),
            density = density,
            layoutDirection = layoutDirection
        )
    }
    val lineColor = remember(lineNumber) {
        getLineColorByNumber(lineNumber)
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
            color = lineColor,
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
    isLastItem: Boolean,
    disabled: Boolean = false,
    lineNumber: Int,
    arrivalTime: String? = null // New parameter for arrival time
) {
    val color = getLineColorByNumber(lineNumber)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(78.dp)
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
            lineNumber = lineNumber
        )
        if (arrivalTime != null) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = arrivalTime,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
