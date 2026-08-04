package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.rounded.DirectionsRailway
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ma.tehro.domain.path.Place
import com.ma.tehro.domain.line.Station
import com.ma.tehro.feature.shortestpath.selection.NearbyType

fun LazyListScope.SearchResults(
    searchQuery: String,
    stations: List<Station>,
    places: List<Place>,
    onStationSelected: (Station) -> Unit,
    onPlaceSelected: (Place) -> Unit,
    onNearMeClick: (NearbyType) -> Unit,
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
                            Icons.Outlined.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    title = "ایستگاه‌های نزدیک من",
                    subtitle = "پیدا کردن ایستگاه‌های نزدیک به موقعیت شما",
                    onClick = { onNearMeClick(NearbyType.Stations) }
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
                            Icons.Outlined.LocationCity,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    title = "مکان‌های نزدیک من",
                    subtitle = "پیدا کردن مکان‌های نزدیک به موقعیت شما",
                    onClick = { onNearMeClick(NearbyType.Places) }
                )

                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.16f)
                )
            }
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
