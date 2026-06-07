package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.FloatingToolbarContainer
import com.ma.tehro.common.ui.ToolbarIconButton
import com.ma.thero.resources.Res
import com.ma.thero.resources.route
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectionToolbar(
    modifier: Modifier = Modifier,
    onFindPathClick: () -> Unit,
    onTimeChangeClick: () -> Unit,
    onDayOfWeekClick: () -> Unit,
    onFindNearestStationClick: () -> Unit,
    onFindNearestStationsByPlaceClick: () -> Unit,
) {
    FloatingToolbarContainer(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        content = {
            ToolbarIconButton(
                icon = Icons.Filled.Business,
                label = "مکان",
                onClick = onFindNearestStationsByPlaceClick
            )
            ToolbarIconButton(
                icon = Icons.Filled.LocationOn,
                label = "ایستگاه",
                onClick = onFindNearestStationClick
            )
            ToolbarIconButton(
                icon = Icons.Filled.CalendarMonth,
                label = "روز",
                onClick = onDayOfWeekClick
            )
            ToolbarIconButton(
                icon = Icons.Filled.Timer,
                label = "زمان",
                onClick = onTimeChangeClick
            )
        },
        fab = {
            FloatingActionButton(
                onClick = onFindPathClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.route),
                    contentDescription = "مسیر",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}