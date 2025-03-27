package com.ma.tehro.ui.train_schedule

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.DraggableTabRow
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.common.fractionToTime
import com.ma.tehro.common.getLineColorByNumber
import com.ma.tehro.common.setStatusBarColor
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.data.ScheduleType
import com.ma.tehro.data.StationName
import com.ma.tehro.data.repo.GroupedScheduleInfo
import com.ma.tehro.ui.theme.Gray
import kotlinx.coroutines.launch

@Composable
fun TrainSchedule(
    modifier: Modifier = Modifier,
    faStationName: String,
    lineNumber: Int,
    state: TrainScheduleState,
    onBack: () -> Unit,
    onScheduleTypeSelected: (StationName, ScheduleType?) -> Unit
) {
    val lineColor = remember { getLineColorByNumber(lineNumber) }
    val window = (LocalView.current.context as Activity).window

    DisposableEffect(lineColor) {
        setStatusBarColor(window, lineColor.toArgb())
        onDispose {
            setStatusBarColor(window, Gray.toArgb())
        }
    }

    Column(modifier = modifier) {
        Appbar(
            title = createBilingualMessage(
                fa = "زمان‌بندی حرکت قطار برای ایستگاه $faStationName",
                en = "train schedule for ${state.stationName}"
            ),
            handleBack = true,
            onBackClick = onBack,
            modifier = Modifier.height(43.dp),
            backgroundColor = lineColor
        )
        when {
            state.schedules.isNotEmpty() -> AppBarDetail(
                schedules = state.schedules,
                processedSchedules = state.processedSchedules,
                initialScrollPositions = state.initialScrollPositions,
                onScheduleTypeSelected = onScheduleTypeSelected,
                selectedScheduleTypes = state.selectedScheduleTypes,
                lineColor = lineColor,
                currentTimeAsDouble = state.currentTimeAsDouble
            )

            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            }

            else ->
                EmptyState(
                    modifier = Modifier.fillMaxSize(),
                    faMessage = "هیچ زمان‌بندی‌ای برای این ایستگاه ثبت نشده. به‌احتمالِ زیاد، ایستگاه غیرفعال است",
                    enMessage = "No schedule is available for this station. It is most likely inactive.",
                    faces = EmptyStatesFaces.sad
                )
        }
    }
}

@Composable
fun AppBarDetail(
    modifier: Modifier = Modifier,
    schedules: List<GroupedScheduleInfo>,
    processedSchedules: Map<StationName, List<ScheduleSection>>,
    initialScrollPositions: Map<StationName, Int?>,
    selectedScheduleTypes: Map<StationName, ScheduleType?>,
    onScheduleTypeSelected: (StationName, ScheduleType?) -> Unit,
    lineColor: Color,
    currentTimeAsDouble: Double,
) {
    val destinations = remember(schedules) { schedules.map { it.destination } }

    DraggableTabRow(
        modifier = modifier.fillMaxWidth(),
        tabsList = destinations,
        lineColor = lineColor,
        onTabSelected = { page, lazyListState ->
            val currentSchedule = schedules.getOrNull(page)
            if (currentSchedule != null) {
                val destination = currentSchedule.destination
                ScheduleList(
                    scheduleInfo = currentSchedule,
                    processedSections = processedSchedules[destination] ?: emptyList(),
                    initialScrollPosition = initialScrollPositions[destination],
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
    initialScrollPosition: Int?,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    selectedType: ScheduleType?,
    onScheduleTypeSelected: (ScheduleType?) -> Unit,
    currentTimeAsDouble: Double,
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(initialScrollPosition) {
        initialScrollPosition?.let { position ->
            coroutineScope.launch {
                lazyListState.scrollToItem(index = position, scrollOffset = -300)
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
            HorizontalDivider()

            LazyColumn(
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

@Composable
private fun TimeListItem(
    time: Double,
    currentTimeAsDouble: Double,
    isCurrentDaySchedule: Boolean,
    isFirstActiveTime: Boolean,
    modifier: Modifier = Modifier,
) {
    val isPastTime = time <= currentTimeAsDouble
    val contentAlpha = when {
        !isCurrentDaySchedule -> 0.5f
        isPastTime -> 0.5f
        else -> 1f
    }

    val remainingTimeText = when {
        !isCurrentDaySchedule -> ""
        isPastTime -> ""
        isFirstActiveTime -> {
            LaunchedEffect(Unit) {
                snapshotFlow { remainingTime(time) }
//                    .distinctUntilChanged()
                    .collect { }
            }
            remainingTime(time)
        }

        else -> {
            remember(time) {
                remainingTime(time)
            }
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isFirstActiveTime && isCurrentDaySchedule)
                    MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ARRIVES AT ${fractionToTime(time)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
            )
            Spacer(Modifier.height(4.dp))
            if (remainingTimeText.isNotEmpty()) {
                Text(
                    text = "$remainingTimeText REMAINING",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFirstActiveTime)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "ساعت ${fractionToTime(time).toFarsiNumber()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
            )
            Spacer(Modifier.height(4.dp))
            if (remainingTimeText.isNotEmpty()) {
                Text(
                    text = "زمان باقی مانده ${remainingTimeText.toFarsiNumber()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFirstActiveTime)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                )
            }
        }
    }
    HorizontalDivider()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScheduleTypeChips(
    scheduleTypes: List<ScheduleType>,
    selectedType: ScheduleType?,
    onScheduleTypeSelected: (ScheduleType?) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(bottom = 8.dp, top = 10.dp, start = 0.dp, end = 0.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        scheduleTypes.forEach { scheduleType ->
            ScheduleDaySelection(
                isSelected = selectedType == scheduleType,
                onClick = {
                    if (selectedType != scheduleType) {
                        onScheduleTypeSelected(scheduleType)
                    }
                },
                label = bilingualScheduleTypeTitle(scheduleType)
            )
        }
    }
}

fun remainingTime(target: Double): String {
    val calendar = android.icu.util.Calendar.getInstance()
    // quick impl: current time in seconds since start of day
    val currentSeconds = calendar.get(android.icu.util.Calendar.HOUR_OF_DAY) * 3600 +
            calendar.get(android.icu.util.Calendar.MINUTE) * 60 +
            calendar.get(android.icu.util.Calendar.SECOND)
    val currentTimeFraction = currentSeconds / 86400.0

    val remaining = target - currentTimeFraction
    if (remaining <= 0) return "00:00:00"

    val totalSeconds = (remaining * 86400).toInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return StringBuilder(8)
        .append(hours.toString().padStart(2, '0'))
        .append(':')
        .append(minutes.toString().padStart(2, '0'))
        .append(':')
        .append(seconds.toString().padStart(2, '0'))
        .toString()
}

private fun bilingualScheduleTypeTitle(scheduleType: ScheduleType): String {
    return when (scheduleType) {
        ScheduleType.SATURDAY_TO_WEDNESDAY -> createBilingualMessage(
            fa = "شنبه تا چهارشنبه",
            en = "Saturday to Wednesday"
        )

        ScheduleType.THURSDAY -> createBilingualMessage(
            fa = "پنجشنبه",
            en = "Thursday"
        )

        ScheduleType.FRIDAY -> createBilingualMessage(
            fa = "جمعه",
            en = "Friday"
        )

        ScheduleType.ALL_DAY -> createBilingualMessage(
            fa = "همه روزه",
            en = "All Days"
        )

        ScheduleType.SATURDAY_TO_THURSDAY -> createBilingualMessage(
            fa = "شنبه تا پنجشنبه",
            en = "Saturday to Thursday"
        )

        ScheduleType.HOLIDAYS_AND_FRIDAY -> createBilingualMessage(
            fa = "جمعه و تعطیلات",
            en = "Holidays And Friday"
        )
    }
}
