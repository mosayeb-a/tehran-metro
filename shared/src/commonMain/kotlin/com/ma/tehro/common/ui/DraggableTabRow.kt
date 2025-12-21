package com.ma.tehro.common.ui

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.ma.tehro.data.BilingualName
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun DraggableTabRow(
    modifier: Modifier = Modifier,
    tabsList: List<BilingualName>,
    lineColor: Color,
    onTabSelected: @Composable (page: Int, lazyListState: LazyListState) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { tabsList.size },
        initialPage = 0
    )
    val listStates = List(tabsList.size) { index ->
        index to rememberSaveable(
            saver = LazyListState.Saver,
            key = "lazyListState_$index"
        ) {
            LazyListState()
        }
    }.toMap()

    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }
    val coroutineScope = rememberCoroutineScope()
    val indication: Indication = LocalIndication.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            with(this) {
                val tabWidth = maxWidth / tabsList.size

                val indicatorOffset =
                    remember(selectedTabIndex.value, pagerState.currentPageOffsetFraction) {
                    val currentTab = selectedTabIndex.value
                    val targetTab = when {
                        pagerState.currentPageOffsetFraction > 0 -> (currentTab + 1).coerceAtMost(tabsList.size - 1)
                        pagerState.currentPageOffsetFraction < 0 -> (currentTab - 1).coerceAtLeast(0)
                        else -> currentTab
                    }

                    val targetOffset = tabWidth * targetTab
                    val currentOffset = tabWidth * currentTab

                    lerp(
                        start = currentOffset,
                        stop = targetOffset,
                        fraction = pagerState.currentPageOffsetFraction.absoluteValue
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(lineColor),
                    horizontalArrangement = Arrangement.Start
                ) {
                    tabsList.forEachIndexed { tabIndex, name ->
                        val interactionSource =
                            remember { MutableInteractionSource() }

                        Box(
                            modifier = Modifier
                                .width(tabWidth)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = indication
                                ) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(tabIndex)
                                    }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BilingualText(
                                fa = "به سمت ${name.fa}",
                                en = "To ${name.en}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (tabIndex == selectedTabIndex.value) {
                                        MaterialTheme.colorScheme.onBackground
                                    } else {
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = .5f)
                                    },
                                ),
                                spaceBetween = (-3).dp,
                                maxLine = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .width(tabWidth)
                        .align(Alignment.BottomStart)
                        .offset(x = indicatorOffset)
                ) {
                    TabIndicator(
                        modifier = Modifier
                            .padding(horizontal = 34.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            listStates[page]?.let { state ->
                onTabSelected(page, state)
            }
        }
    }
}

@Composable
fun TabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Spacer(
        modifier
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}