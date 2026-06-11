package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.ma.tehro.common.ui.TehroHorizontalDivider
import com.ma.tehro.common.ui.drawVerticalScrollbar
import com.ma.tehro.common.ui.theme.Red
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.common.NearbyStation
import com.ma.tehro.domain.line.Station
import com.ma.tehro.feature.shortestpath.selection.components.DaySelectorSheet
import com.ma.tehro.feature.shortestpath.selection.components.LineChangeDelaySlider
import com.ma.tehro.feature.shortestpath.selection.components.NearbyStationSheet
import com.ma.tehro.feature.shortestpath.selection.components.SelectionToolbar
import com.ma.tehro.feature.shortestpath.selection.components.StationField
import com.ma.tehro.feature.shortestpath.selection.components.TimePickerDialog
import com.ma.tehro.feature.shortestpath.selection.components.rememberPulseAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelector(
    viewState: StationSelectionState,
    searchQuery: String,
    stations: List<Station>,
    onSearchQueryChanged: (q: String) -> Unit,
    onFindPathClick: (
        fromEn: String, toEn: String, fromFa: String, toFa: String,
        lineChangeDelayMinutes: Int, dayOfWeek: Int, currentTime: Double
    ) -> Unit,
    onSelectedChange: (isFrom: Boolean, query: String, faQuery: String) -> Unit,
    onBack: () -> Unit,
    onFindNearestStationAsStart: (onError: () -> Unit) -> Unit,
    onNearestStationChanged: (NearbyStation) -> Unit,
    onLineChangeDelayChanged: (Int) -> Unit,
    onTimeChanged: (Double) -> Unit,
    onDayOfWeekChanged: (Int) -> Unit,
    onFindNearestStationsByPlace: () -> Unit,
    checkLocationPermission: (onGranted: () -> Unit) -> Unit,
) {
    var showNearestStations by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    var showDaySelector by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val startPulse = rememberPulseAnimation()
    val destPulse = rememberPulseAnimation()

    val startNodeColor by animateColorAsState(
        targetValue = if (startPulse.isAnimating) Red else MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
        animationSpec = tween(durationMillis = 300)
    )

    val destNodeColor by animateColorAsState(
        targetValue = if (destPulse.isAnimating) Red else MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
        animationSpec = tween(durationMillis = 300)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                Appbar(
                    fa = "مسیریابی",
                    en = "Path Finder",

                    onBackClick = onBack
                )
                TehroHorizontalDivider()
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawVerticalScrollbar(lazyListState),
                state = lazyListState,
            ) {
                item { Spacer(Modifier.height(28.dp)) }

                item {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        StationField(
                            selectedStation = BilingualName(
                                viewState.selectedEnStartStation,
                                viewState.selectedFaStartStation
                            ),
                            searchQuery = searchQuery,
                            onSearchQueryChanged = onSearchQueryChanged,
                            stations = stations,
                            onStationSelected = { en, fa -> onSelectedChange(true, en, fa) },
                            isFrom = true,
                            nodeColor = startNodeColor,
                            nodeScale = startPulse.scale.value
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        StationField(
                            selectedStation = BilingualName(
                                viewState.selectedEnDestStation,
                                viewState.selectedFaDestStation
                            ),
                            searchQuery = searchQuery,
                            onSearchQueryChanged = onSearchQueryChanged,
                            stations = stations,
                            onStationSelected = { en, fa -> onSelectedChange(false, en, fa) },
                            isFrom = false,
                            nodeColor = destNodeColor,
                            nodeScale = destPulse.scale.value
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
                        if (isStartEmpty) {
                            startPulse.trigger()
                        }
                        if (isDestEmpty) {
                            destPulse.trigger()
                        }
                        if (isSameStation) {
                            startPulse.trigger()
                            destPulse.trigger()
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
                    checkLocationPermission {
                        if (!viewState.findNearestLocationProgress) {
                            onFindNearestStationAsStart {
                                showNearestStations = false
                            }
                        }
                        showNearestStations = true
                    }
                },
                onFindNearestStationsByPlaceClick = onFindNearestStationsByPlace,
            )

            if (showNearestStations) {
                NearbyStationSheet(
                    locationName = "موقعیت شما",
                    nearbyStations = viewState.nearbyStations,
                    isLoading = viewState.findNearestLocationProgress,
                    onStationSelected = { station ->
                        onNearestStationChanged(station)
                        showNearestStations = false
                    },
                    selectedStation = viewState.selectedNearbyStation,
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