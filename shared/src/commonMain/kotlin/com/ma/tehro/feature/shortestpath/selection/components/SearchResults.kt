package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.rounded.DirectionsRailway
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ma.tehro.data.place.Place
import com.ma.tehro.domain.line.Station


fun LazyListScope.SearchResults(
    stations: List<Station>,
    places: List<Place>,
    placesNearMe: List<Place>,
    searchQuery: String,
    isLoadingNearbyPlaces: Boolean,
    onStationSelected: (Station) -> Unit,
    onPlaceSelected: (Place) -> Unit,
    onNearMeClick: () -> Unit,
    onMapClick: () -> Unit,
    onDismiss: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
) {
    if (searchQuery.isBlank()) {
        item(
            key = "quick_actions"
        ) {
            Column {
                QuickActionItem(
                    icon = {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    title = "ایستگاه‌های نزدیک من",
                    subtitle = "پیدا کردن ایستگاه‌های نزدیک به موقعیت شما",
                    onClick = onNearMeClick
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme
                        .onSecondaryContainer.copy(alpha = 0.12f)
                )

                QuickActionItem(
                    icon = {
                        Icon(
                            Icons.Outlined.Map,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    title = "انتخاب از روی نقشه",
                    subtitle = "انتخاب روی نقشه برای یافتن ایستگاه‌های نزدیک",
                    onClick = onMapClick
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme
                        .onSecondaryContainer.copy(alpha = 0.12f)
                )

                QuickActionItem(
                    icon = {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    title = "مکان‌های نزدیک من",
                    subtitle = "پیدا کردن مکان‌های نزدیک به موقعیت شما",
                    onClick = {
                    }
                )

                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.16f)
                )
            }
        }
    }

    if (isLoadingNearbyPlaces) {
        item(
            key = "loading_places"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "در حال پیدا کردن مکان‌ها...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    } else if (placesNearMe.isNotEmpty() && searchQuery.isBlank()) {
        stickyHeader(
            key = "nearby_places_header"
        ) {
            StickyHeader(
                icon = Icons.Rounded.LocationCity,
                title = "مکان‌های نزدیک شما"
            )
        }

        items(
            items = placesNearMe,
            key = { "nearby_${it.name}_${it.latitude}_${it.longitude}" }
        ) { place ->
            PlaceItem(
                place = place,
                onClick = {
                    onPlaceSelected(place)
                }
            )
        }
    }

    if (stations.isNotEmpty()) {
        stickyHeader(
            key = "stations_header"
        ) {
            StickyHeader(
                icon = Icons.Rounded.DirectionsRailway,
                title = "ایستگاه‌ها"
            )
        }

        items(
            items = stations,
            key = { "station_${it.name}" }
        ) { station ->
            StationItem(
                station = station,
                onClick = {
                    onStationSelected(station)
                    onDismiss()
                    onSearchQueryChanged("")
                }
            )
        }
    }

    if (places.isNotEmpty()) {
        stickyHeader(
            key = "places_header"
        ) {
            StickyHeader(
                icon = Icons.Rounded.LocationCity,
                title = "مکان‌ها"
            )
        }

        items(
            items = places,
            key = { "place_${it.name}_${it.latitude}_${it.longitude}" }
        ) { place ->
            PlaceItem(
                place = place,
                onClick = {
                    onPlaceSelected(place)
                }
            )
        }
    }

    item(
        key = "bottom_spacer"
    ) {
        Spacer(modifier = Modifier.height(16.dp))
    }
}
