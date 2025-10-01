package com.ma.tehro.feature.shortestpath.pathfinder

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.ui.drawVerticalScrollbar
import com.ma.tehro.data.Station
import com.ma.tehro.data.repo.PathItem
import com.ma.tehro.feature.shortestpath.pathfinder.components.Appbar
import com.ma.tehro.feature.shortestpath.pathfinder.components.PathfinderFloatingToolbar
import com.ma.tehro.feature.shortestpath.pathfinder.components.PinableTitle
import com.ma.tehro.feature.shortestpath.pathfinder.components.StationRow

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    onInfoClick: () -> Unit,
    onMetroMapClick: (shortestPath: List<String>) -> Unit,
    lineChangeDelayMinutes: Int,
) {
    val titleIndices = remember(state.shortestPath) {
        state.shortestPath.mapIndexedNotNull { index, item ->
            if (item is PathItem.Title) index to item else null
        }
    }
    val exitAlwaysScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)

    Scaffold(
        modifier = modifier.nestedScroll(exitAlwaysScrollBehavior),
        containerColor = MaterialTheme.colorScheme.secondary,
        topBar = {
            Appbar(
                fromEn = fromEn,
                toEn = toEn,
                onBack = onBack,
                fromFa = fromFa,
                toFa = toFa,
                estimatedTime = state.estimatedTime,
                lineChangeDelayMinutes = lineChangeDelayMinutes
            )
        },
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
                modifier = Modifier.drawVerticalScrollbar(lazyListState),
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
                                    arrivalTime = state.stationTimes[item.station.name]
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

                item {
                    Spacer(Modifier.height(87.dp))
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
                            isFirstItem = title == state.shortestPath[0],
                            lineNumber = title.en[5].digitToInt()
                        )
                    }
                }
            }

            PathfinderFloatingToolbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                lazyListState = lazyListState,
                onInfoClick = onInfoClick,
                onMapClick = {
                    onMetroMapClick(
                        state.shortestPath.mapNotNull { item ->
                            (item as? PathItem.StationItem)?.station?.translations?.fa
                        }
                    )
                },
                scrollBehavior = exitAlwaysScrollBehavior
            )
        }
    }
}