package com.ma.tehro.feature.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.DraggableTabRow
import com.ma.tehro.common.ui.EmptyStatesFaces
import com.ma.tehro.common.ui.Message
import com.ma.tehro.common.ui.TehroHorizontalDivider
import com.ma.tehro.common.ui.drawVerticalScrollbar
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.schedule.StationSchedule
import com.ma.tehro.domain.schedule.ScheduleType
import com.ma.tehro.feature.schedule.components.ScheduleTypeChips
import com.ma.tehro.feature.schedule.components.TimeListItem
import kotlinx.coroutines.launch

@Composable
fun TrainSchedule(
    modifier: Modifier = Modifier,
    station: BilingualName,
    lineNumber: Int,
    state: TrainScheduleState,
    onBack: () -> Unit,
    onScheduleTypeSelected: (BilingualName, ScheduleType?) -> Unit
) {
    val lineColor = remember { getLineColorByNumber(lineNumber) }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column(
                modifier = Modifier
                    .background(lineColor)
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
            ) {
                Appbar(
                    fa = "زمان‌بندی ایستگاه ${station.fa}",
                    en = "schedule for ${station.en}",
                    onBackClick = onBack,
                    modifier = Modifier.height(43.dp),
                    backgroundColor = lineColor
                )
                if (state.schedules.isNotEmpty()) {
                    Content(
                        schedules = state.schedules,
                        selectedTypes = state.selectedTypes,
                        onScheduleTypeSelected = onScheduleTypeSelected,
                        lineColor = lineColor,
                        currentTime = state.currentTime
                    )
                }
            }
        }
    ) { padding ->
        padding.let {}
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            }

            else ->
                Message(
                    modifier = Modifier.fillMaxSize(),
                    faMessage = "زمان‌بندی‌ای برای این ایستگاه موجود نیست. به نظر می‌رسد ایستگاه غیرفعال است",
                    faces = EmptyStatesFaces.sad
                )
        }
    }
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    schedules: Map<BilingualName, List<ScheduleSection>>,
    selectedTypes: Map<BilingualName, ScheduleType?>,
    onScheduleTypeSelected: (BilingualName, ScheduleType?) -> Unit,
    lineColor: Color,
    currentTime: Double,
) {
    val destinations = remember(schedules) { schedules.keys.toList() }

    DraggableTabRow(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(),
        tabsList = destinations,
        lineColor = lineColor,
        onTabSelected = { page, lazyListState ->
            val destination = destinations.getOrNull(page)
            if (destination != null) {
                val sections = schedules[destination] ?: emptyList()
                val selectedType = selectedTypes[destination]

                ScheduleList(
                    sections = sections,
                    selectedType = selectedType,
                    lazyListState = lazyListState,
                    onScheduleTypeSelected = { scheduleType ->
                        onScheduleTypeSelected(destination, scheduleType)
                    },
                    currentTime = currentTime,
                )
            }
        }
    )
}

@Composable
private fun ScheduleList(
    sections: List<ScheduleSection>,
    selectedType: ScheduleType?,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    onScheduleTypeSelected: (ScheduleType?) -> Unit,
    currentTime: Double,
) {
    val coroutineScope = rememberCoroutineScope()
    val availableTypes = remember(sections) { sections.map { it.type }.distinct() }

    LaunchedEffect(selectedType, sections) {
        val sectionsToShow = if (selectedType != null) {
            sections.filter { it.type == selectedType }
        } else {
            sections
        }

        var targetIndex = 0
        var found = false

        sectionsToShow.forEach { section ->
            if (!found && section.isCurrentDay) {
                val firstActiveTimeIndex = section.times.indexOfFirst { it > currentTime }
                if (firstActiveTimeIndex != -1) {
                    targetIndex += firstActiveTimeIndex
                    found = true
                } else {
                    targetIndex += section.times.size
                }
            } else {
                targetIndex += section.times.size
            }
            targetIndex += 1
        }

        if (found) {
            coroutineScope.launch {
                lazyListState.scrollToItem(index = targetIndex, scrollOffset = -300)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            ScheduleTypeChips(
                scheduleTypes = availableTypes,
                selectedType = selectedType,
                onScheduleTypeSelected = onScheduleTypeSelected
            )
            TehroHorizontalDivider()

            LazyColumn(
                modifier = Modifier.drawVerticalScrollbar(lazyListState),
                state = lazyListState,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                val sectionsToShow = if (selectedType != null) {
                    sections.filter { it.type == selectedType }
                } else {
                    sections
                }

                sectionsToShow.forEach { section ->
                    items(
                        items = section.times,
                        key = { time -> "${section.type.name}_$time" }
                    ) { time ->
                        val isFirstActiveTime = remember(section.times, currentTime) {
                            section.isCurrentDay &&
                                    time == section.times.firstOrNull { it > currentTime }
                        }

                        TimeListItem(
                            time = time,
                            currentTimeAsDouble = currentTime,
                            isCurrentDaySchedule = section.isCurrentDay,
                            isFirstActiveTime = isFirstActiveTime
                        )
                    }

                    item(key = "divider_${section.type.name}") {
                        Spacer(modifier = Modifier.height(58.dp))
                    }
                }
            }
        }
    }
}