package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.common.ui.timelineview.TimelineView
import com.ma.tehro.common.ui.timelineview.TimelineView.SingleNode
import com.ma.tehro.data.Station
import com.ma.tehro.domain.BilingualName

@Composable
fun StationField(
    selectedStation: BilingualName?,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    stations: List<Station>,
    onStationSelected: (en: String, fa: String) -> Unit,
    isFrom: Boolean,
    nodeColor: Color,
    nodeScale: Float
) {
    SearchableBottomSheet(
        stations = stations,
        selectedStation = selectedStation,
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        onStationSelected = { station ->
            onStationSelected(station.name, station.translations.fa)
        },
        dropdownItem = { station ->
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
        },
        modifier = Modifier.fillMaxWidth(),
        startContent = {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                SingleNode(
                    nodeColor = nodeColor,
                    lineColor = nodeColor,
                    nodeType = if (isFrom) TimelineView.NodeType.FIRST else TimelineView.NodeType.LAST,
                    nodeSize = 20f,
                    isChecked = true,
                    lineWidth = 5.2f,
                    isDashed = true,
                    scale = nodeScale
                )
                Text(
                    text = if (isFrom) "مبدا" + "\n" + "FROM" else "مقصد" + "\n" + "TO",
                    modifier = Modifier
                        .padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                    ),
                    textAlign = TextAlign.Center
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = Color.Black
        )
    )
}