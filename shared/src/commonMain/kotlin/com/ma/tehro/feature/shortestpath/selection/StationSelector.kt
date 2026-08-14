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
import com.ma.tehro.domain.path.Place
import com.ma.tehro.domain.common.BilingualName
import com.ma.tehro.domain.line.Station
import com.ma.tehro.feature.shortestpath.selection.components.DaySelectorSheet
import com.ma.tehro.feature.shortestpath.selection.components.LineChangeDelaySlider
import com.ma.tehro.feature.shortestpath.selection.components.SelectionToolbar
import com.ma.tehro.feature.shortestpath.selection.components.StationSelectorSheet
import com.ma.tehro.feature.shortestpath.selection.components.StationTextField
import com.ma.tehro.feature.shortestpath.selection.components.TimePickerDialog
import com.ma.tehro.feature.shortestpath.selection.components.rememberPulseAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelector(
    viewState: StationSelectorState,
    searchQuery: String,
    stations: List<Station>,
    places: List<Place>,
    onSearchQueryChanged: (q: String) -> Unit,
    onFindPath: (from: BilingualName, to: BilingualName, delay: Int, dayOfWeek: Int, time: Double) -> Unit,
    onSelectStation: (isFrom: Boolean, station: BilingualName) -> Unit,
    onSearchNearby: (NearbySource, NearbyType) -> Unit,
    onDelayChange: (Int) -> Unit,
    onTimeChanged: (Double) -> Unit,
    onDayOfWeekChanged: (Int) -> Unit,
    onCheckPermission: (onGranted: () -> Unit) -> Unit,
    onMapClick: (isFrom: Boolean) -> Unit,
    onMetroGuideClick: () -> Unit,
    onBack: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    var showDaySelector by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var isFromSheetOpen by remember { mutableStateOf<Boolean?>(null) }

    val fromPulse = rememberPulseAnimation()
    val toPulse = rememberPulseAnimation()

    val fromColor by animateColorAsState(
        targetValue = if (fromPulse.isAnimating) Red else
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
        animationSpec = tween(durationMillis = 300)
    )
    val toColor by animateColorAsState(
        targetValue = if (toPulse.isAnimating) Red else
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
                            nodeColor = fromColor,
                            nodeScale = fromPulse.scale.value,
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
                            nodeColor = toColor,
                            nodeScale = toPulse.scale.value,
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
                            fromPulse.trigger()
                            toPulse.trigger()
                        }

                        isStartEmpty -> fromPulse.trigger()
                        isDestEmpty -> toPulse.trigger()
                        isSameStation -> {
                            fromPulse.trigger()
                            toPulse.trigger()
                        }

                        else -> onFindPath(
                            startStation,
                            destStation,
                            viewState.transferDelay,
                            viewState.dayOfWeek,
                            viewState.departureTime
                        )
                    }
                },
                onTimeChangeClick = { showTimePicker = true },
                onDayOfWeekClick = { showDaySelector = true },
                onMetroGuideClick = onMetroGuideClick
            )

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
                    initialHour = (viewState.departureTime * 24).toInt(),
                    initialMinute = ((viewState.departureTime * 24 * 60) % 60).toInt()
                )
            }

            if (isFromSheetOpen != null) {
                StationSelectorSheet(
                    stations = filteredStations,
                    places = places,
                    nearbyState = viewState.nearby,
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onStationSelected = { station ->
                        onSelectStation(
                            isFromSheetOpen == true,
                            BilingualName(station.name, station.translations.fa)
                        )
                        isFromSheetOpen = null
                    },
                    onPlaceSelected = { place ->
                        onSearchNearby(
                            NearbySource.Place(place),
                            NearbyType.Stations
                        )
                    },
                    onSearchNearby = { request, content, onPermissionGranted ->
                        if (request is NearbySource.CurrentLocation) {
                            onCheckPermission {
                                onSearchNearby(request, content)
                                onPermissionGranted()
                            }
                        } else {
                            onSearchNearby(request, content)
                            onPermissionGranted()
                        }
                    },
                    onMapClick = { onMapClick(isFromSheetOpen == true) },
                    onDismiss = {
                        isFromSheetOpen = null
                    },
                )
            }
        }
    }
}