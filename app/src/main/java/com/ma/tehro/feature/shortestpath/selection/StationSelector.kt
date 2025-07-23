package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.domain.NearestStation
import com.ma.tehro.feature.shortestpath.selection.components.DaySelectorSheet
import com.ma.tehro.feature.shortestpath.selection.components.LineChangeDelaySlider
import com.ma.tehro.feature.shortestpath.selection.components.NearestStationSheet
import com.ma.tehro.feature.shortestpath.selection.components.SelectionToolbar
import com.ma.tehro.feature.shortestpath.selection.components.StationDropdown
import com.ma.tehro.feature.shortestpath.selection.components.TimePickerDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelector(
    viewState: StationSelectionState,
    onFindPathClick: (
        fromEn: String, toEn: String, fromFa: String, toFa: String,
        lineChangeDelayMinutes: Int, dayOfWeek: Int, currentTime: Double
    ) -> Unit,
    onSelectedChange: (isFrom: Boolean, query: String, faQuery: String) -> Unit,
    onBack: () -> Unit,
    onFindNearestStationAsStart: () -> Unit,
    onNearestStationChanged: (NearestStation) -> Unit,
    onLineChangeDelayChanged: (Int) -> Unit,
    onTimeChanged: (Double) -> Unit,
    onDayOfWeekChanged: (Int) -> Unit,
    onFindNearestStationsByPlace: () -> Unit,
) {
    var showNearestStations by remember { mutableStateOf(false) }
    var hasTriggeredNearestSearch by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    var showDaySelector by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var triggerStartPulse by remember { mutableStateOf(false) }
    var triggerDestPulse by remember { mutableStateOf(false) }

    val startScale = remember { Animatable(1f) }
    val destScale = remember { Animatable(1f) }

    val startNodeColor by animateColorAsState(
        targetValue = if (triggerStartPulse) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary.copy(
            alpha = 0.9f
        ),
        animationSpec = tween(durationMillis = 300)
    )
    val destNodeColor by animateColorAsState(
        targetValue = if (triggerDestPulse) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary.copy(
            alpha = 0.9f
        ),
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(triggerStartPulse) {
        if (triggerStartPulse) {
            launch {
                startScale.animateTo(
                    targetValue = 1.3f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 2000f)
                )
                startScale.animateTo(
                    targetValue = 0.8f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 2000f)
                )
                startScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 2000f)
                )
                triggerStartPulse = false
            }
        }
    }

    LaunchedEffect(triggerDestPulse) {
        if (triggerDestPulse) {
            launch {
                destScale.animateTo(
                    targetValue = 1.3f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 2000f)
                )
                destScale.animateTo(
                    targetValue = 0.8f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 2000f)
                )
                destScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 2000f)
                )
                triggerDestPulse = false
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        topBar = {
            Column {
                Appbar(
                    fa = "مسیریابی",
                    en = "Path Finder",
                    handleBack = true,
                    onBackClick = onBack
                )
                HorizontalDivider()
            }
        },
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = it,
                state = lazyListState
            ) {
                item { Spacer(Modifier.height(28.dp)) }

                item {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        StationDropdown(
                            query = "${viewState.selectedFaStartStation}\n${viewState.selectedEnStartStation}",
                            stations = viewState.stations,
                            onStationSelected = { en, fa -> onSelectedChange(true, en, fa) },
                            isFrom = true,
                            nodeColor = startNodeColor,
                            nodeScale = startScale.value
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        StationDropdown(
                            query = "${viewState.selectedFaDestStation}\n${viewState.selectedEnDestStation}",
                            stations = viewState.stations,
                            isFrom = false,
                            onStationSelected = { en, fa -> onSelectedChange(false, en, fa) },
                            nodeColor = destNodeColor,
                            nodeScale = destScale.value
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }

                item {
                    LineChangeDelaySlider(
                        lineChangeDelay = viewState.lineChangeDelayMinutes,
                        onLineChangeDelayChanged = { delay -> onLineChangeDelayChanged(delay) },
                    )
                }

                item { Spacer(Modifier.height(73.dp)) }
            }
            SelectionToolbar(
                modifier = Modifier.align(Alignment.BottomEnd),
                onFindPathClick = {
                    val isStartEmpty = viewState.selectedEnStartStation.isEmpty()
                    val isDestEmpty = viewState.selectedEnDestStation.isEmpty()
                    val isSameStation =
                        viewState.selectedEnStartStation == viewState.selectedEnDestStation

                    if (isStartEmpty || isDestEmpty || isSameStation) {
                        if (isStartEmpty) triggerStartPulse = true
                        if (isDestEmpty) triggerDestPulse = true
                        if (isSameStation) {
                            triggerStartPulse = true
                            triggerDestPulse = true
                        }
                    } else {
                        onFindPathClick(
                            viewState.selectedEnStartStation,
                            viewState.selectedEnDestStation,
                            viewState.selectedFaStartStation,
                            viewState.selectedFaDestStation,
                            viewState.lineChangeDelayMinutes,
                            viewState.dayOfWeek,
                            viewState.currentTime
                        )
                    }
                },
                onTimeChangeClick = { showTimePicker = true },
                onDayOfWeekClick = { showDaySelector = true },
                onFindNearestStationClick = {
                    if (!viewState.findNearestLocationProgress) {
                        onFindNearestStationAsStart()
                        hasTriggeredNearestSearch = true
                    }
                    showNearestStations = true
                },
                onFindNearestStationsByPlaceClick = onFindNearestStationsByPlace,
            )

            if (showNearestStations) {
                NearestStationSheet(
                    nearestStations = viewState.nearestStations,
                    isLoading = viewState.findNearestLocationProgress && hasTriggeredNearestSearch,
                    onStationSelected = { station ->
                        onNearestStationChanged(station)
                        showNearestStations = false
                    },
                    selectedStation = viewState.selectedNearestStation,
                    onDismiss = { showNearestStations = false }
                )
            }

            if (showDaySelector) {
                DaySelectorSheet(
                    selectedDay = viewState.dayOfWeek,
                    onDismiss = { showDaySelector = false },
                    onDaySelected = { day -> onDayOfWeekChanged(day) }
                )
            }

            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    onConfirm = { hour, minute ->
                        val newTime = (hour * 3600 + minute * 60).toDouble() / 86400.0
                        onTimeChanged(newTime)
                        showTimePicker = false
                    },
                    initialHour = (viewState.currentTime * 24).toInt(),
                    initialMinute = ((viewState.currentTime * 24 * 60) % 60).toInt()
                )
            }
        }
    }
}