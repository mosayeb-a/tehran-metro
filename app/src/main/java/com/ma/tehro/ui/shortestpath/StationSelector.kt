package com.ma.tehro.ui.shortestpath

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.Appbar
import com.ma.tehro.common.timelineview.TimelineView
import com.ma.tehro.common.timelineview.TimelineView.SingleNode
import com.ma.tehro.data.Station

@Composable
fun StationSelector(
    viewState: PathUiState,
    onFindPathClick: (fromEn: String, toEn: String, fromFa: String, toFa: String) -> Unit,
    onSelectedChange: (isFrom: Boolean, query: String, faQuery: String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            Column {
                Appbar(
                    title = "مسیریابی" + "\n" + "Path Finder",
                    handleBack = true,
                    onBackClick = onBack
                )
                HorizontalDivider()
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(it)
                .fillMaxWidth()
        ) {
            StationDropdown(
                query = viewState.selectedEnStartStation,
                stations = viewState.stations,
                onStationSelected = { en, fa -> onSelectedChange(true, en, fa) },
                isFrom = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            StationDropdown(
                query = viewState.selectedEnDestStation,
                stations = viewState.stations,
                onStationSelected = { en, fa -> onSelectedChange(false, en, fa) },
                isFrom = false
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                Text(text = "یافتن مسیر\nFind Path")
            }

        }
    }
}

@Composable
fun StationDropdown(
    query: String,
    stations: Map<String, Station>,
    onStationSelected: (en: String, fa: String) -> Unit,
    isFrom: Boolean
) {
    var selectedStation by rememberSaveable { mutableStateOf(query) }

    SearchableExpandedDropDownMenu(
        listOfItems = stations.entries.toList(),
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .fillMaxWidth(),
        onDropDownItemSelected = { entry ->
            selectedStation = entry.key
            onStationSelected(entry.key, entry.value.translations.fa)
        },
        dropdownItem = { entry ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                        .padding(start = 4.dp)
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
