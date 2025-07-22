package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.toFarsiNumber
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.domain.NearestStation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun NearestStationSheet(
    nearestStations: List<NearestStation>,
    isLoading: Boolean,
    selectedStation: NearestStation?,
    onStationSelected: (NearestStation) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        ),
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        NearestStationsBottomSheetContent(
            nearestStations = nearestStations,
            isLoading = isLoading,
            onStationSelected = onStationSelected,
            selectedStation = selectedStation,
            onDismiss = onDismiss
        )
    }
}


@Composable
fun NearestStationsBottomSheetContent(
    modifier: Modifier = Modifier,
    selectedStation: NearestStation?,
    nearestStations: List<NearestStation>,
    isLoading: Boolean,
    onStationSelected: (NearestStation) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        if (isLoading && nearestStations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(42.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    nearestStations.size,
                    key = { nearestStations[it].station.name }) { index ->
                    val station = nearestStations[index]
                    val isSelected = selectedStation?.station?.name == station.station.name

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.large)
                            .background(
                                if (isSelected)
                                    MaterialTheme.colorScheme.tertiary
                                else
                                    MaterialTheme.colorScheme.secondaryContainer
                            )
                            .clickable {
                                onStationSelected(station)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = station.distanceNumberFa,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = station.distanceUnitFa,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                BilingualText(
                                    fa = station.station.translations.fa,
                                    en = station.station.name.uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = (index + 1).toFarsiNumber(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

