package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.common.ui.TehroSearchBar
import com.ma.tehro.common.ui.drawVerticalScrollbar
import com.ma.tehro.data.place.Place
import com.ma.tehro.domain.common.NearbyStation
import com.ma.tehro.domain.line.Station

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelectorSheet(
    stations: List<Station>,
    places: List<Place>,
    placesNearMe: List<Place>,
    stationsNearMe: List<NearbyStation>,
    placeNearbyStations: List<NearbyStation>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onStationSelected: (Station) -> Unit,
    onPlaceSelected: (Place) -> Unit,
    onNearMeClick: () -> Unit,
    onMapClick: () -> Unit,
    isLoadingNearbyPlaces: Boolean,
    isLoadingNearbyStations: Boolean,
    isLoadingStationsByPlace: Boolean,
    onDismiss: () -> Unit,
) {
    var mode by remember {
        mutableStateOf<StationSearchMode>(StationSearchMode.Search)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    val nearbyStations = when (val currentMode = mode) {
        is StationSearchMode.Nearby -> {
            when (currentMode.source) {
                NearbySource.CurrentLocation -> stationsNearMe
                NearbySource.Place -> placeNearbyStations
            }
        }

        else -> emptyList()
    }

    val isLoadingNearby = when (val currentMode = mode) {
        is StationSearchMode.Nearby -> {
            when (currentMode.source) {
                NearbySource.CurrentLocation -> isLoadingNearbyStations
                NearbySource.Place -> isLoadingStationsByPlace
            }
        }

        else -> false
    }

    val locationName = when (val currentMode = mode) {
        is StationSearchMode.Nearby -> currentMode.locationName
        else -> ""
    }

    val isSearchMode = mode is StationSearchMode.Search

    val cornerRadius by animateDpAsState(
        targetValue = if (isSearchMode) 0.dp else 32.dp,
        animationSpec = tween(durationMillis = 400)
    )

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
            onSearchQueryChanged("")
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(
            topStart = cornerRadius,
            topEnd = cornerRadius,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        dragHandle = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = isSearchMode,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                TehroSearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = "جستجوی ایستگاه یا مکان...",
                )
            }

            AnimatedContent(
                targetState = mode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith
                            fadeOut(animationSpec = tween(250))
                },
                label = "StationSearchMode"
            ) { currentMode ->
                when (currentMode) {
                    is StationSearchMode.Search -> {
                        val listState = rememberLazyListState()
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .drawVerticalScrollbar(listState)
                        ) {
                            SearchResults(
                                stations = stations,
                                places = places,
                                placesNearMe = placesNearMe,
                                searchQuery = searchQuery,
                                isLoadingNearbyPlaces = isLoadingNearbyPlaces,
                                onStationSelected = onStationSelected,
                                onPlaceSelected = { place ->
                                    mode = StationSearchMode.Nearby(
                                        source = NearbySource.Place,
                                        locationName = place.name
                                    )
                                    onPlaceSelected(place)
                                },
                                onNearMeClick = {
                                    mode = StationSearchMode.Nearby(
                                        source = NearbySource.CurrentLocation,
                                        locationName = "موقعیت فعلی"
                                    )
                                    onNearMeClick()
                                },
                                onMapClick = onMapClick,
                                onDismiss = onDismiss,
                                onSearchQueryChanged = onSearchQueryChanged
                            )
                        }
                    }

                    is StationSearchMode.Nearby -> {
                        NearbyStations(
                            locationName = locationName.toFarsiNumber(),
                            nearbyStations = nearbyStations,
                            isLoading = isLoadingNearby,
                            onStationSelected = { nearbyStation ->
                                onStationSelected(
                                    Station(
                                        name = nearbyStation.station.name,
                                        translations = nearbyStation.station.translations,
                                    )
                                )
                                onDismiss()
                            },
                            onBack = {
                                mode = StationSearchMode.Search
                            },
                            onDismiss = onDismiss
                        )
                    }
                }
            }
        }
    }
}