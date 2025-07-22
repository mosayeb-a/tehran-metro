package com.ma.tehro.feature.shortestpath.places

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.feature.shortestpath.AppSearchBar
import com.ma.tehro.feature.shortestpath.selection.components.NearestStationSheet

@Composable
fun PlaceSelection(
    modifier: Modifier = Modifier,
    viewState: PlaceSelectionState,
    onPlaceClick: (lat: Double, long: Double) -> Unit,
    onStationSelected: (String, String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onBack: () -> Unit,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondary,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Appbar(
                    fa = "انتخاب مکان برای مشاهده ایستگاه‌های نزدیک",
                    en = "select a place to see near stations",
                    handleBack = true,
                    onBackClick = onBack
                )
                Spacer(Modifier.height(8.dp))
                AppSearchBar(
                    modifier = Modifier
                        .padding(vertical = 6.dp),
                    value = viewState.searchQuery,
                    onValueChange = { query -> onSearchQueryChanged(query) },
                    placeholder = "جست‌وجوی مکان دلخواه",
                )
                Spacer(Modifier.height(4.dp))
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(top = 170.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                bottom = paddingValues.calculateBottomPadding(),
                start = paddingValues.calculateLeftPadding(LayoutDirection.Rtl),
                end = paddingValues.calculateRightPadding(LayoutDirection.Rtl)
            )
        ) {
            viewState.places.forEach { items ->
                stickyHeader {
                    Text(
                        text = items.category.value,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.tertiary)
                            .padding(8.dp),
                        textAlign = TextAlign.End
                    )
                }
                items(items.places) { place ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPlaceClick(place.latitude, place.longitude)
                                showBottomSheet = true
                            }
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = place.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.W500
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }

    if (showBottomSheet) {
        NearestStationSheet(
            nearestStations = viewState.nearbyStations,
            isLoading = viewState.isLoading,
            selectedStation = null,
            onStationSelected = { nearStation ->
                onStationSelected(nearStation.station.name, nearStation.station.translations.fa)
                showBottomSheet = false
            },
            onDismiss = { showBottomSheet = false }
        )
    }
}