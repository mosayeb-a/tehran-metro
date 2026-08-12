package com.ma.tehro.feature.map.city.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.path.Nearby
import com.ma.tehro.feature.shortestpath.selection.NearbySearchState
import com.ma.tehro.feature.shortestpath.selection.NearbyType
import com.ma.tehro.feature.shortestpath.selection.components.NearbyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyStationsSheet(
    nearbyStations: List<Nearby<Station>>,
    onStationSelected: (Station) -> Unit,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        dragHandle = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        NearbyList(
            locationName = "موقعیت انتخاب شده",
            nearbyState = NearbySearchState(
                stations = nearbyStations,
                type = NearbyType.Stations
            ),
            items = nearbyStations,
            onItemSelected = { station ->
                onStationSelected(station)
            },
            onBack = onDismiss,
            onDismiss = onDismiss,
            onRetry = onRetry,
            itemContent = { item, _ ->
                BilingualText(
                    fa = item.translations.fa,
                    en = item.name.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp
                    )
                )
            }
        )
    }
}