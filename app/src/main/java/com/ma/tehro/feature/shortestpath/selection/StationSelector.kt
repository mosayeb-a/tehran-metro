package com.ma.tehro.feature.shortestpath.selection

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ma.tehro.R
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.createBilingualMessage
import com.ma.tehro.common.timelineview.TimelineView
import com.ma.tehro.common.timelineview.TimelineView.SingleNode
import com.ma.tehro.data.Station
import com.ma.tehro.services.NearestStation

@Composable
fun StationSelector(
    viewState: StationSelectionState,
    onFindPathClick: (fromEn: String, toEn: String, fromFa: String, toFa: String) -> Unit,
    onSelectedChange: (isFrom: Boolean, query: String, faQuery: String) -> Unit,
    onBack: () -> Unit,
    findNearestStationAsStart: () -> Unit,
    onNearestStationChanged: (NearestStation?) -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expandedMenu) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "rotate arrow"
    )
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            Column {
                Appbar(
                    title = createBilingualMessage("مسیریابی", "Path Finder"),
                    handleBack = true,
                    onBackClick = onBack
                )
                HorizontalDivider()
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(it)
                .fillMaxWidth()
        ) {
            item(1) {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        modifier = Modifier.padding(start = 12.dp),
                        onClick = {
                            findNearestStationAsStart()
                        },
                        shape = RoundedCornerShape(
                            topStart = 36.dp,
                            bottomStart = 36.dp,
                            topEnd = if (viewState.nearestStations.isEmpty()) 36.dp else 0.dp,
                            bottomEnd = if (viewState.nearestStations.isEmpty()) 36.dp else 0.dp
                        ),
                        enabled = !viewState.findNearestLocationProgress,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = .6f)
                        ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.location_on_24px),
                                contentDescription = "find nearby stations"
                            )
                            Text(
                                text = "یافتن نزدیک‌ترین ایستگاه\nFind nearest stations",
                                style = MaterialTheme.typography.labelMedium
                            )

                            if (viewState.findNearestLocationProgress && viewState.nearestStations.isEmpty()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    if (!viewState.findNearestLocationProgress && viewState.nearestStations.isNotEmpty()) {
                        VerticalDivider(modifier = Modifier.fillMaxHeight())
                        Box {
                            TextButton(
                                modifier = Modifier.padding(end = 12.dp),
                                onClick = { expandedMenu = true },
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    bottomStart = 0.dp,
                                    topEnd = 36.dp,
                                    bottomEnd = 36.dp
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(
                                        alpha = .6f
                                    )
                                ),
                            ) {
                                Text(
                                    text = viewState.nearestStations.first()
                                        .let { info -> "${info.station.name}\n${info.station.translations.fa}" },
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(Modifier.width(2.dp))
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .rotate(rotationAngle),
                                    painter = painterResource(R.drawable.arrow_drop_down_24px),
                                    contentDescription = "more nearby station"
                                )
                            }
                            if (expandedMenu) {
                                DropdownMenu(
                                    expanded = expandedMenu,
                                    onDismissRequest = {
                                        expandedMenu = false
                                    },
                                ) {
                                    viewState.nearestStations.forEach { info ->
                                        DropdownMenuItem(
                                            onClick = {
                                                expandedMenu = false
                                                onNearestStationChanged(info)
                                            },
                                            text = {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(vertical = 4.dp)
                                                        .fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(
                                                            text = info.station.translations.fa,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                        )
                                                        Text(
                                                            text = info.station.name,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                        )
                                                    }
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(start = 8.dp),
                                                        text = "فاصله: ${info.distanceTextFa}\ndistance: ${info.distanceTextEn}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        textAlign = TextAlign.End
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item(2) {
                Spacer(Modifier.height(16.dp))
            }

            item(3) {
                StationDropdown(
                    query = "${viewState.selectedFaStartStation}\n${viewState.selectedEnStartStation}",
                    stations = viewState.stations,
                    onStationSelected = { en, fa -> onSelectedChange(true, en, fa) },
                    isFrom = true
                )
            }

            item(4) {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item(5) {
                StationDropdown(
                    query = "${viewState.selectedFaDestStation}\n${viewState.selectedEnDestStation}",
                    stations = viewState.stations,
                    isFrom = false,
                    onStationSelected = { en, fa -> onSelectedChange(false, en, fa) }
                )
            }

            item(6) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item(7) {
                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(76.dp),
                    onClick = {
                        onFindPathClick(
                            viewState.selectedEnStartStation,
                            viewState.selectedEnDestStation,
                            viewState.selectedFaStartStation,
                            viewState.selectedFaDestStation,
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = .5f)
                    ),
                    enabled = viewState.selectedEnStartStation.isNotEmpty() &&
                            viewState.selectedEnDestStation.isNotEmpty()
                ) {
                    Text(text = createBilingualMessage(fa = "یافتن مسیر", en = "Find Path"))
                }
            }
        }
    }
}

@Composable
fun StationDropdown(
    query: String,
    stations: Map<String, Station>,
    onStationSelected: (en: String, fa: String) -> Unit,
    isFrom: Boolean,
) {
    var selectedStation by rememberSaveable { mutableStateOf(query.split("\n").getOrNull(1)) }

    SearchableExpandedDropDownMenu(
        listOfItems = stations.entries.toList(),
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .fillMaxWidth(),
        onDropDownItemSelected = { entry ->
            selectedStation = entry.key
            onStationSelected(entry.key, entry.value.translations.fa)
        },
        initialValue = query,
        dropdownItem = { entry ->
            Row(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${entry.value.translations.fa}\n${entry.value.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        defaultItem = { defaultStation ->
            selectedStation = defaultStation.key
        },
        startContent = {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                SingleNode(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = .8f),
                    nodeType = if (isFrom) TimelineView.NodeType.FIRST else TimelineView.NodeType.LAST,
                    nodeSize = 20f,
                    isChecked = true,
                    lineWidth = 5.2f,
                    isDashed = true
                )
                Text(
                    text = if (isFrom) "مبدا" + "\n" + "FROM" else "مقصد" + "\n" + "TO",
                    modifier = Modifier
                        .padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        onSearchTextFieldClicked = {},
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = Color.Black
        ),
        searchPredicate = { searchText, entry ->
            entry.value.name.contains(searchText, ignoreCase = true) ||
                    entry.value.translations.fa.contains(searchText, ignoreCase = true)
        }
    )
}
