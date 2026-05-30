package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.ui.BilingualText
import com.ma.tehro.common.ui.SearchableBottomSheet
import com.ma.tehro.common.ui.timelineview.TimelineView
import com.ma.tehro.common.ui.timelineview.TimelineView.SingleNode
import com.ma.tehro.domain.line.Station
import com.ma.tehro.domain.common.BilingualName

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
    var isSheetOpen by remember { mutableStateOf(false) }

    val filteredStations = if (searchQuery.isBlank()) {
        stations
    } else {
        stations.filter { station ->
            station.name.contains(searchQuery, ignoreCase = true) ||
                    station.translations.fa.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black
            ),
            value = "",
            readOnly = true,
            onValueChange = {},
            leadingIcon = {
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
            placeholder = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedStation?.fa ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = selectedStation?.en?.uppercase() ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 11.sp,
                        color = Color.Black.copy(alpha = .9f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { isSheetOpen = true }) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(32.dp),
            interactionSource = remember { MutableInteractionSource() }.also { source ->
                LaunchedEffect(source) {
                    source.interactions.collect {
                        if (it is PressInteraction.Release) isSheetOpen = true
                    }
                }
            }
        )
    }

    SearchableBottomSheet(
        items = filteredStations,
        searchQuery = searchQuery,
        onSearchQueryChanged = onSearchQueryChanged,
        onItemSelected = { station ->
            onStationSelected(station.name, station.translations.fa)
        },
        searchPlaceholder = "جستجوی ایستگاه دلخواه",
        itemKey = { it.name },
        isOpen = isSheetOpen,
        onDismiss = { isSheetOpen = false },
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