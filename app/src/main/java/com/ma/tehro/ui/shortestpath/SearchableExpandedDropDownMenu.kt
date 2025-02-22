package com.ma.tehro.ui.shortestpath

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ma.tehro.data.Station

@Composable
fun <T> SearchableExpandedDropDownMenu(
    modifier: Modifier = Modifier,
    listOfItems: List<T>,
    enable: Boolean = true,
    readOnly: Boolean = true,
    openedIcon: ImageVector = Icons.Outlined.KeyboardArrowUp,
    closedIcon: ImageVector = Icons.Outlined.KeyboardArrowDown,
    parentTextFieldCornerRadius: Dp = 12.dp,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    onDropDownItemSelected: (T) -> Unit = {},
    dropdownItem: @Composable (T) -> Unit,
    isError: Boolean = false,
    showDefaultSelectedItem: Boolean = false,
    defaultItemIndex: Int = 0,
    initialValue: String = "",
    defaultItem: (T) -> Unit,
    onSearchTextFieldClicked: () -> Unit,
    startContent: @Composable (() -> Unit) = { },
    searchPredicate: (String, T) -> Boolean = { searchText, item ->
        item.toString().contains(searchText, ignoreCase = true)
    }
) {
    var selectedOptionText by remember { mutableStateOf(initialValue) }
    var searchedOption by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var filteredItems = mutableListOf<T>()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val itemHeights = remember { mutableStateMapOf<Int, Int>() }
    val baseHeight = 260.dp
    val density = LocalDensity.current

    if (showDefaultSelectedItem) {
        selectedOptionText = selectedOptionText.ifEmpty { listOfItems[defaultItemIndex].toString() }

        defaultItem(
            listOfItems[defaultItemIndex],
        )
    }

    LaunchedEffect(initialValue) { selectedOptionText = initialValue }

    val maxHeight = remember(itemHeights.toMap()) {
        if (itemHeights.keys.toSet() != listOfItems.indices.toSet()) {

            return@remember baseHeight
        }
        val baseHeightInt = with(density) { baseHeight.toPx().toInt() }


        var sum = with(density) { DropdownMenuVerticalPadding.toPx().toInt() } * 2
        for ((_, itemSize) in itemHeights.toSortedMap()) {
            sum += itemSize
            if (sum >= baseHeightInt) {
                return@remember with(density) { (sum - itemSize / 2).toDp() }
            }
        }

        baseHeight
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            modifier = modifier
                .height(76.dp)
                .fillMaxWidth(),
            colors = colors,
            value = "",
            readOnly = readOnly,
            enabled = enable,
            onValueChange = { selectedOptionText = it },
            leadingIcon = {
                startContent.invoke()
            },
            placeholder = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = selectedOptionText.split("\n").getOrNull(0) ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = selectedOptionText.split("\n").getOrNull(1) ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            },
            trailingIcon = {
                IconToggleButton(
                    checked = expanded,
                    onCheckedChange = {
                        expanded = it
                    },
                ) {
                    if (expanded) {
                        Icon(
                            imageVector = openedIcon,
                            contentDescription = null,
                        )
                    } else {
                        Icon(
                            imageVector = closedIcon,
                            contentDescription = null,
                        )
                    }
                }
            },
            shape = RoundedCornerShape(parentTextFieldCornerRadius),
            isError = isError,
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
//                        keyboardController?.show()
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                expanded = !expanded
                            }
                        }
                    }
                },
        )
        if (expanded) {
            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .requiredSizeIn(maxHeight = maxHeight),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedTextField(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .focusRequester(focusRequester),
                        value = searchedOption,
                        colors = OutlinedTextFieldDefaults
                            .colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                cursorColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        onValueChange = { selectedSport ->
                            searchedOption = selectedSport
                            filteredItems = listOfItems.filter { item ->
                                searchPredicate(searchedOption, item)
                            }.toMutableList()
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                        },
                        maxLines = 1,
                        placeholder = {
                            Text(text = "Search")
                        },
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    focusRequester.requestFocus()
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) {
                                            onSearchTextFieldClicked()
                                        }
                                    }
                                }
                            },
                    )

                    val items = if (filteredItems.isEmpty()) {
                        listOfItems
                    } else {
                        filteredItems
                    }

                    items.forEach { selectedItem ->
                        DropdownMenuItem(
                            onClick = {
                                keyboardController?.hide()
                                if (selectedItem is Map.Entry<*, *> && selectedItem.value is Station) {
                                    val station = selectedItem.value as Station
                                    selectedOptionText =
                                        "${station.name}\n${station.translations.fa}"
                                    onDropDownItemSelected(selectedItem)
                                }
                                searchedOption = ""
                                expanded = false
                            },
                            text = {
                                dropdownItem(selectedItem)
                            },
                        )
                    }
                }
            }
        }
    }
}

private val DropdownMenuVerticalPadding = 8.dp
