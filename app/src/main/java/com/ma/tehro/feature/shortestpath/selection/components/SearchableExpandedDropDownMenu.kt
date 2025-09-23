package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ma.tehro.common.ui.drawVerticalScrollbar
import com.ma.tehro.data.Station
import com.ma.tehro.feature.shortestpath.AppSearchBar
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
    colors: TextFieldColors = TextFieldDefaults.colors(),
    onDropDownItemSelected: (T) -> Unit = {},
    dropdownItem: @Composable (T) -> Unit,
    isError: Boolean = false,
    showDefaultSelectedItem: Boolean = false,
    defaultItemIndex: Int = 0,
    initialValue: String = "",
    defaultItem: (T) -> Unit = {},
    startContent: @Composable (() -> Unit) = {},
    searchPredicate: (String, T) -> Boolean
) {
    var selectedOptionText by rememberSaveable { mutableStateOf(initialValue) }
    var searchedOption by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var debouncedSearch by remember { mutableStateOf("") }

    LaunchedEffect(initialValue) {
        selectedOptionText = initialValue
    }

    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    val displayedItems by remember(debouncedSearch, listOfItems) {
        derivedStateOf {
            if (debouncedSearch.isEmpty()) listOfItems
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

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
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
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = placeholderText.second?.uppercase() ?: "",
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
            shape = RoundedCornerShape(32.dp),
            isError = isError,
            interactionSource = remember { MutableInteractionSource() }.also { source ->
                LaunchedEffect(source) {
                    source.interactions.collect {
                        if (it is PressInteraction.Release) expanded = !expanded
                    }
                }
            }
        )
    }

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.8f),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column {
                    AppSearchBar(
                        modifier = Modifier
                            .padding(vertical = 16.dp),
                        value = searchedOption,
                        onValueChange = { searchedOption = it },
                        placeholder = "جست‌وجوی ایستگاه‌ دلخواه",
                    )

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .drawVerticalScrollbar(listState)
                    ) {
                        items(
                            items = displayedItems,
                            key = { it.toString() }
                        ) { item ->
                            Column {
                                DropdownMenuItem(
                                    text = { dropdownItem(item) },
                                    onClick = {
                                        keyboardController?.hide()
                                        if (item is Map.Entry<*, *> && item.value is Station) {
                                            val station = item.value as Station
                                            selectedOptionText =
                                                "${station.translations.fa}\n${station.name}"
                                            onDropDownItemSelected(item as T)
                                        }
                                        searchedOption = ""
                                        expanded = false
                                    },
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    )
                                )
                                if (item != displayedItems.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}