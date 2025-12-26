package com.ma.tehro.feature.train_schedule

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.ma.tehro.common.ui.theme.LightGray
import com.ma.tehro.data.BilingualName
import com.ma.tehro.data.ScheduleType
import com.ma.tehro.data.repo.GroupedScheduleInfo
import com.ma.tehro.feature.train_schedule.components.ScheduleTypeChips
import com.ma.tehro.feature.train_schedule.components.TimeListItem
import kotlinx.coroutines.launch

@Composable
fun TrainSchedule(
    modifier: Modifier = Modifier,
    faStationName: String,
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
                    fa = "زمان‌بندی حرکت قطار برای ایستگاه $faStationName",
                    en = "train schedule for ${state.stationName}",
                    onBackClick = onBack,
                    modifier = Modifier.height(43.dp),
                    backgroundColor = lineColor
                )
                if (state.schedules.isNotEmpty()) {
                    Content(
                        schedules = state.schedules,
                        processedSchedules = state.processedSchedules,
                        onScheduleTypeSelected = onScheduleTypeSelected,
                        selectedScheduleTypes = state.selectedScheduleTypes,
                        lineColor = lineColor,
                        currentTimeAsDouble = state.currentTimeAsDouble
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
                    faMessage = "هیچ زمان‌بندی‌ای برای این ایستگاه ثبت نشده. به‌احتمالِ زیاد، ایستگاه غیرفعال است",
                    faces = EmptyStatesFaces.sad
                )
        }
    }
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    schedules: List<GroupedScheduleInfo>,
    processedSchedules: Map<BilingualName, List<ScheduleSection>>,
    selectedScheduleTypes: Map<BilingualName, ScheduleType?>,
    onScheduleTypeSelected: (BilingualName, ScheduleType?) -> Unit,
    lineColor: Color,
    currentTimeAsDouble: Double,
) {
    val destinations = remember(schedules) { schedules.map { it.destination } }

    DraggableTabRow(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(),
        tabsList = destinations,
        lineColor = lineColor,
        onTabSelected = { page, lazyListState ->
            val currentSchedule = schedules.getOrNull(page)
            if (currentSchedule != null) {
                val destination = currentSchedule.destination
                ScheduleList(
                    scheduleInfo = currentSchedule,
                    processedSections = processedSchedules[destination] ?: emptyList(),
                    lazyListState = lazyListState,
                    selectedType = selectedScheduleTypes[destination],
                    onScheduleTypeSelected = { scheduleType ->
                        onScheduleTypeSelected(destination, scheduleType)
                    },
                    currentTimeAsDouble = currentTimeAsDouble,
                )
            }
        }
    )
}

@Composable
private fun ScheduleList(
    scheduleInfo: GroupedScheduleInfo,
    processedSections: List<ScheduleSection>,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    selectedType: ScheduleType?,
    onScheduleTypeSelected: (ScheduleType?) -> Unit,
    currentTimeAsDouble: Double,
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        val sectionsToShow = if (selectedType != null) {
            processedSections.filter { it.type == selectedType }
        } else {
            processedSections
        }

        var targetIndex = 0
        var found = false

        sectionsToShow.forEach { section ->
            if (!found && section.isCurrentDay) {
                val firstActiveTimeIndex = section.times.indexOfFirst { it > currentTimeAsDouble }
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
                scheduleTypes = scheduleInfo.schedules.keys.toList(),
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
                    processedSections.filter { it.type == selectedType }
                } else {
                    processedSections
                }

                sectionsToShow.forEach { section ->
                    items(
                        items = section.times,
                        key = { time -> "${section.type.name}_$time" }
                    ) { time ->
                        val isFirstActiveTime = remember(section.times, currentTimeAsDouble) {
                            section.isCurrentDay &&
                                    time == section.times.firstOrNull { it > currentTimeAsDouble }
                        }

                        TimeListItem(
                            time = time,
                            currentTimeAsDouble = currentTimeAsDouble,
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