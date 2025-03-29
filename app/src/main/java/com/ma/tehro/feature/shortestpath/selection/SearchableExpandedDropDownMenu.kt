package com.ma.tehro.feature.shortestpath.selection


import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.isFarsi
import com.ma.tehro.data.Station
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    defaultItem: (T) -> Unit = {},
    onSearchTextFieldClicked: () -> Unit = {},
    startContent: @Composable (() -> Unit) = {},
    searchPredicate: (String, T) -> Boolean = { searchText, item ->
        item.toString().contains(searchText, ignoreCase = true)
    }
) {

    var selectedOptionText by rememberSaveable { mutableStateOf(initialValue) }
    var searchedOption by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var debouncedSearch by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val displayedItems by remember(debouncedSearch, listOfItems) {
        derivedStateOf {
            if (debouncedSearch.isEmpty()) listOfItems.take(40)
            else listOfItems.filter { searchPredicate(debouncedSearch, it) }
        }
    }

    if (showDefaultSelectedItem && selectedOptionText.isEmpty()) {
        LaunchedEffect(Unit) {
            selectedOptionText = listOfItems[defaultItemIndex].toString()
            defaultItem(listOfItems[defaultItemIndex])
        }
    }


    LaunchedEffect(searchedOption) {
        coroutineScope.launch {
            delay(250)
            debouncedSearch = searchedOption
        }
    }


    val placeholderText by remember(selectedOptionText) {
        derivedStateOf {
            val parts = selectedOptionText.split("\n")
            parts.getOrNull(0) to parts.getOrNull(1)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier
                .height(76.dp)
                .fillMaxWidth(),
            colors = colors,
            value = "",
            readOnly = readOnly,
            enabled = enable,
            onValueChange = {},
            leadingIcon = { startContent() },
            placeholder = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = placeholderText.first ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = placeholderText.second ?: "",
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
                    onCheckedChange = { expanded = it }
                ) {
                    Icon(
                        imageVector = if (expanded) openedIcon else closedIcon,
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(parentTextFieldCornerRadius),
            isError = isError,
            interactionSource = remember { MutableInteractionSource() }.also { source ->
                LaunchedEffect(source) {
                    source.interactions.collect {
                        if (it is PressInteraction.Release) expanded = !expanded
                    }
                }
            }
        )

        if (expanded) {
            val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
            val dynamicHeight = maxOf(screenHeightDp * 0.6f, 260.dp)
            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(dynamicHeight),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Column{
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .focusRequester(focusRequester),
                        value = searchedOption,
                        onValueChange = { searchedOption = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            cursorColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            textDirection = if (isFarsi(searchedOption)) TextDirection.Rtl else TextDirection.Ltr,
                            textAlign = if (isFarsi(searchedOption)) TextAlign.Right else TextAlign.Left
                        ),
                        leadingIcon = { Icon(Icons.Outlined.Search, null) },
                        maxLines = 1,
                        placeholder = { Text("Search") },
                        interactionSource = remember { MutableInteractionSource() }.also { source ->
                            LaunchedEffect(source) {
                                focusRequester.requestFocus()
                                source.interactions.collect {
                                    if (it is PressInteraction.Release) onSearchTextFieldClicked()
                                }
                            }
                        }
                    )

                    displayedItems.forEach { selectedItem ->
                        DropdownMenuItem(
                            contentPadding = PaddingValues(vertical = 10.dp),
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
                            text = { dropdownItem(selectedItem) }
                        )
                    }
                }
            }
        }
    }
}