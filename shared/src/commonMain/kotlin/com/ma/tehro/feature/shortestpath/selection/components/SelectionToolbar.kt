package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.FloatingToolbarContainer
import com.ma.thero.resources.Res
import com.ma.thero.resources.route
import org.jetbrains.compose.resources.painterResource

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
            IconButton(modifier = Modifier.size(48.dp), onClick = onFindNearestStationsByPlaceClick) {
                Icon(Icons.Filled.Business, contentDescription = null, tint = Color.White)
            }
            IconButton(modifier = Modifier.size(48.dp), onClick = onFindNearestStationClick) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White)
            }
            IconButton(modifier = Modifier.size(48.dp), onClick = onDayOfWeekClick) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Color.White)
            }
            IconButton(modifier = Modifier.size(48.dp), onClick = onTimeChangeClick) {
                Icon(Icons.Filled.Timer, contentDescription = null, tint = Color.White)
            }
        },
        fab = {
            FloatingActionButton(
                onClick = onFindPathClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(Res.drawable.route),
                    contentDescription = "Find shortest path"
                )
            }
        }
    )
}