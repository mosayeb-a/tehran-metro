package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.EmptyStatesFaces
import com.ma.tehro.common.ui.Message
import com.ma.tehro.domain.path.PathHistory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PathHistorySheet(
    history: List<PathHistory>,
    onSelect: (PathHistory) -> Unit,
    onDelete: (PathHistory) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "تاریخچه مسیریابی" },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(8.dp))

            if (history.isEmpty()) {
                Message(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(182.dp),
                    message = "هنوز مسیری جستجو نکرده‌اید",
                    faces = EmptyStatesFaces.suggestion,
                    messageStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                    ),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                ) {
                    items(
                        items = history,
                        key = { "${it.from.name}_${it.to.name}_${it.timestamp}" },
                    ) { item ->
                        PathHistoryItem(
                            from = item.from,
                            to = item.to,
                            onClick = { onSelect(item) },
                            onDelete = { onDelete(item) },
                        )
                    }
                }
            }
        }
    }
}