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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.common.ui.TehroSearchBar
import com.ma.tehro.common.ui.drawVerticalScrollbar
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.Place
import com.ma.tehro.feature.shortestpath.selection.NearbyType
import com.ma.tehro.feature.shortestpath.selection.NearbySource
import com.ma.tehro.feature.shortestpath.selection.NearbySearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelectorSheet(
    stations: List<Station>,
    places: List<Place>,
    nearbyState: NearbySearchState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onStationSelected: (Station) -> Unit,
    onPlaceSelected: (Place) -> Unit,
    onSearchNearby: (source: NearbySource, type: NearbyType, onReady: () -> Unit) -> Unit,
    onMapClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    var mode by remember {
        mutableStateOf<StationSearchMode>(StationSearchMode.Search)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )
    val listState = rememberLazyListState()

    val isSearchMode = mode is StationSearchMode.Search

    val cornerRadius by animateDpAsState(
        targetValue = if (isSearchMode) 0.dp else 42.dp,
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
                                searchQuery = searchQuery,
                                onStationSelected = onStationSelected,
                                onPlaceSelected = { place ->
                                    onSearchNearby(
                                        NearbySource.Place(place),
                                        NearbyType.Stations
                                    ) {
                                        mode = StationSearchMode.Nearby(
                                            source = NearbySource.Place(place),
                                            type = NearbyType.Stations,
                                            locationName = place.name
                                        )
                                    }
                                },
                                onNearMeClick = { content ->
                                    onSearchNearby(
                                        NearbySource.CurrentLocation,
                                        content
                                    ) {
                                        mode = StationSearchMode.Nearby(
                                            source = NearbySource.CurrentLocation,
                                            type = content,
                                            locationName = "موقعیت فعلی شما"
                                        )
                                    }
                                },
                                onMapClick = onMapClick,
                                onDismiss = onDismiss,
                                onSearchQueryChanged = onSearchQueryChanged
                            )
                        }
                    }

                    is StationSearchMode.Nearby -> {
                        when (currentMode.type) {
                            NearbyType.Stations -> {
                                NearbyList(
                                    locationName = currentMode.locationName.toFarsiNumber(),
                                    nearbyState = nearbyState,
                                    items = nearbyState.stations,
                                    onItemSelected = { station ->
                                        onStationSelected(
                                            Station(
                                                name = station.name,
                                                translations = station.translations,
                                            )
                                        )
                                        onDismiss()
                                    },
                                    onBack = {
                                        mode = StationSearchMode.Search
                                    },
                                    onDismiss = onDismiss,
                                    onRetry = {
                                        nearbyState.source?.let { request ->
                                            onSearchNearby(request, currentMode.type) {}
                                        }
                                    },
                                    itemContent = { item, _ ->
                                        BilingualText(
                                            fa = item.translations.fa,
                                            en = item.name.uppercase(),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 16.sp
                                            ),
                                        )
                                    }
                                )
                            }

                            NearbyType.Places -> {
                                NearbyList(
                                    locationName = currentMode.locationName.toFarsiNumber(),
                                    nearbyState = nearbyState,
                                    items = nearbyState.places,
                                    onItemSelected = { place ->
                                        onPlaceSelected(place)
                                        onDismiss()
                                    },
                                    onBack = {
                                        mode = StationSearchMode.Search
                                    },
                                    onDismiss = onDismiss,
                                    onRetry = {
                                        nearbyState.source?.let { request ->
                                            onSearchNearby(request, currentMode.type) {}
                                        }
                                    },
                                    itemContent = { item, _ ->
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}