package com.ma.tehro.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableBottomSheet(
    items: List<T>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "جستجو...",
    itemKey: (T) -> Any,
    onDismiss: () -> Unit,
    itemContent: @Composable (T) -> Unit,
    dividerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
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
            modifier = modifier
                .fillMaxWidth()
                .zIndex(0f)
        ) {
            TehroSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .zIndex(1f),
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = searchPlaceholder,
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .drawVerticalScrollbar(listState)
                    .zIndex(0f)
            ) {
                items(
                    items = items,
                    key = { itemKey(it) }
                ) { item ->
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    keyboardController?.hide()
                                    onItemSelected(item)
                                    onDismiss()
                                    onSearchQueryChanged("")
                                }
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 16.dp
                                )
                        ) {
                            itemContent(item)
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = dividerColor
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}