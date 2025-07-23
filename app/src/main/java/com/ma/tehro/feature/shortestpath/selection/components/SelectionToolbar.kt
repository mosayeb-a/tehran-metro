package com.ma.tehro.feature.shortestpath.selection.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ma.tehro.R

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
    var expanded by rememberSaveable { mutableStateOf(true) }

    HorizontalFloatingToolbar(
        expanded = expanded,
        floatingActionButton = {
            FloatingToolbarDefaults.VibrantFloatingActionButton(
                onClick = {
                    onFindPathClick()
                },
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.route),
                    contentDescription = "find shortest path",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        },
        modifier = modifier
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            )
            .offset(y = -ScreenOffset),
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(
            toolbarContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            toolbarContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        content = {
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = { onFindNearestStationsByPlaceClick() },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Filled.Business, contentDescription = "")
            }
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = onFindNearestStationClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = "")
            }
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = { onDayOfWeekClick() },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "")
            }
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = { onTimeChangeClick() },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Filled.Timer, contentDescription = "")
            }
        },
    )
}
