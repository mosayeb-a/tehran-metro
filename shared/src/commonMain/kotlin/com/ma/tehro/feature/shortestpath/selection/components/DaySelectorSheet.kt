package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.BilingualText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySelectorSheet(
    selectedDay: Int,
    onDismiss: () -> Unit,
    onDaySelected: (Int) -> Unit
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        ),
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        DaySelectorBottomSheetContent(
            dayOfWeek = selectedDay,
            onDayOfWeekChangeClick = onDaySelected,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun DaySelectorBottomSheetContent(
    modifier: Modifier = Modifier,
    onDayOfWeekChangeClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    dayOfWeek: Int
) {
    var selectedItem by remember { mutableIntStateOf(dayOfWeek) }

    val days = remember {
        listOf(
            Pair("شنبه" to "SATURDAY", 6),
            Pair("یکشنبه" to "SUNDAY", 7),
            Pair("دوشنبه" to "MONDAY", 1),
            Pair("سه‌شنبه" to "TUESDAY", 2),
            Pair("چهارشنبه" to "WEDNESDAY", 3),
            Pair("پنج‌شنبه" to "THURSDAY", 4),
            Pair("جمعه" to "FRIDAY", 5)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(days.size) { index ->
                val (names, value) = days[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .background(
                            if (selectedItem == value)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primaryContainer
                        )
                        .clickable {
                            selectedItem = value
                            onDayOfWeekChangeClick(value)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = "go",
                        modifier = Modifier.padding(end = 12.dp),
                        tint = if (selectedItem == value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    BilingualText(
                        fa = names.first,
                        en = names.second,
                        modifier = Modifier
                            .weight(1f),
                        textAlign = TextAlign.End,
                        textColor = if (selectedItem == value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
