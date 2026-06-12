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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.common.ui.SearchableBottomSheet
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
import com.ma.tehro.feature.shortestpath.selection.components.StationTextField
import com.ma.tehro.feature.shortestpath.selection.components.TimePickerDialog
import com.ma.tehro.feature.shortestpath.selection.components.rememberPulseAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelector(
    viewState: StationSelectionState,
    searchQuery: String,
    stations: List<Station>,
    onSearchQueryChanged: (q: String) -> Unit,
    onFindPath: (from: BilingualName, to: BilingualName, delay: Int, dayOfWeek: Int, time: Double) -> Unit,
    onSelectStation: (isFrom: Boolean, station: BilingualName) -> Unit,
    onBack: () -> Unit,
    onFindNearest: (onError: () -> Unit) -> Unit,
    onNearestSelected: (NearbyStation) -> Unit,
    onDelayChange: (Int) -> Unit,
    onTimeChanged: (Double) -> Unit,
    onDayOfWeekChanged: (Int) -> Unit,
    onSearchByPlace: () -> Unit,
    onCheckPermission: (onGranted: () -> Unit) -> Unit,
) {
    var showNearestStations by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    var showDaySelector by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var isFromSheetOpen by remember { mutableStateOf<Boolean?>(null) }

    val startPulse = rememberPulseAnimation()
    val destPulse = rememberPulseAnimation()

    val startNodeColor by animateColorAsState(
        targetValue = if (startPulse.isAnimating) Red else
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
        animationSpec = tween(durationMillis = 300)
    )

    val destNodeColor by animateColorAsState(
        targetValue = if (destPulse.isAnimating) Red else
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
        animationSpec = tween(durationMillis = 300)
    )

    val filteredStations = if (searchQuery.isBlank()) {
        stations
    } else {
        stations.filter { station ->
            station.name.contains(searchQuery, ignoreCase = true) ||
                    station.translations.fa.contains(searchQuery, ignoreCase = true)
        }
    }

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
                        StationTextField(
                            selectedStation = viewState.fromStation,
                            isFrom = true,
                            nodeColor = startNodeColor,
                            nodeScale = startPulse.scale.value,
                            onClick = { isFromSheetOpen = true }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        StationTextField(
                            selectedStation = viewState.toStation,
                            isFrom = false,
                            nodeColor = destNodeColor,
                            nodeScale = destPulse.scale.value,
                            onClick = { isFromSheetOpen = false }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }

                item {
                    LineChangeDelaySlider(
                        lineChangeDelay = viewState.transferDelay,
                        onLineChangeDelayChanged = { delay -> onDelayChange(delay) },
                    )
                }

                item { Spacer(Modifier.height(73.dp)) }
            }

            SelectionToolbar(
                modifier = Modifier.align(Alignment.BottomEnd),
                onFindPathClick = {
                    val startStation = viewState.fromStation
                    val destStation = viewState.toStation

                    val isStartEmpty = startStation?.en.isNullOrEmpty()
                    val isDestEmpty = destStation?.en.isNullOrEmpty()
                    val isSameStation =
                        startStation != null && destStation != null && startStation == destStation

                    when {
                        isStartEmpty && isDestEmpty -> {
                            startPulse.trigger()
                            destPulse.trigger()
                        }

                        isStartEmpty -> startPulse.trigger()
                        isDestEmpty -> destPulse.trigger()
                        isSameStation -> {
                            startPulse.trigger()
                            destPulse.trigger()
                        }

                        else -> onFindPath(
                            startStation,
                            destStation,
                            viewState.transferDelay,
                            viewState.dayOfWeek,
                            viewState.currentTime
                        )
                    }
                },
                onTimeChangeClick = { showTimePicker = true },
                onDayOfWeekClick = { showDaySelector = true },
                onFindNearestStationClick = {
                    onCheckPermission {
                        if (!viewState.isSearchingNearby) {
                            onFindNearest {
                                showNearestStations = false
                            }
                        }
                        showNearestStations = true
                    }
                },
                onFindNearestStationsByPlaceClick = onSearchByPlace,
            )

            if (showNearestStations) {
                NearbyStationSheet(
                    locationName = "موقعیت شما",
                    nearbyStations = viewState.nearbyStations,
                    isLoading = viewState.isSearchingNearby,
                    onStationSelected = { station ->
                        onNearestSelected(station)
                        showNearestStations = false
                    },
                    selectedStation = viewState.nearestStation,
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

            if (isFromSheetOpen != null) {
                SearchableBottomSheet(
                    items = filteredStations,
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onItemSelected = { station ->
                        onSelectStation(
                            isFromSheetOpen == true,
                            BilingualName(station.name, station.translations.fa)
                        )
                        isFromSheetOpen = null
                    },
                    searchPlaceholder = "جستجوی ایستگاه دلخواه",
                    itemKey = { it.name },
                    onDismiss = { isFromSheetOpen = null },
                    itemContent = { station ->
                        BilingualText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            fa = station.translations.fa,
                            en = station.name.uppercase(),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLine = 2,
                            textAlign = TextAlign.End
                        )
                    }
                )
            }
        }
    }
}