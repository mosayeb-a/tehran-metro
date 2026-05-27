package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma.tehro.common.ui.TehroSearchBar
import com.ma.tehro.common.ui.drawVerticalScrollbar
import com.ma.tehro.data.Station
import com.ma.tehro.domain.BilingualName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableBottomSheet(
    stations: List<Station>,
    selectedStation: BilingualName?,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onStationSelected: (Station) -> Unit,
    dropdownItem: @Composable (Station) -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    readOnly: Boolean = true,
    openedIcon: ImageVector = Icons.Outlined.KeyboardArrowUp,
    closedIcon: ImageVector = Icons.Outlined.KeyboardArrowDown,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    isError: Boolean = false,
    startContent: @Composable (() -> Unit) = {},
) {
    var isSheetOpen by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    LaunchedEffect(isSheetOpen) {
        if (!isSheetOpen && searchQuery.isNotEmpty()) {
            onSearchQueryChanged("")
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
                        imageVector = if (isSheetOpen) openedIcon else closedIcon,
                        contentDescription = null
                    )
                }
            },
            shape = RoundedCornerShape(32.dp),
            isError = isError,
            interactionSource = remember { MutableInteractionSource() }.also { source ->
                LaunchedEffect(source) {
                    source.interactions.collect {
                        if (it is PressInteraction.Release) isSheetOpen = true
                    }
                }
            }
        )
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                isSheetOpen = false
                onSearchQueryChanged("")
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.3f),
                        thickness = 3.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
            ) {
                TehroSearchBar(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = "جست‌وجوی ایستگاه‌ دلخواه",
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .drawVerticalScrollbar(listState)
                ) {
                    items(
                        items = stations,
                        key = { it.name }
                    ) { station ->
                        Column {
                            DropdownMenuItem(
                                text = { dropdownItem(station) },
                                onClick = {
                                    keyboardController?.hide()
                                    onStationSelected(station)
                                    isSheetOpen = false
                                    onSearchQueryChanged("")
                                },
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                )
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
                            )
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )
                    }
                }
            }
        }
    }
}