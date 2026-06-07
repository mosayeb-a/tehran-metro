package com.ma.tehro.feature.shortestpath.pathfinder.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ma.tehro.common.ui.FloatingToolbarContainer
import com.ma.tehro.common.ui.ToolbarIconButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PathfinderFloatingToolbar(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    onInfoClick: () -> Unit,
    onMapClick: () -> Unit,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
) {
    val scope = rememberCoroutineScope()

    FloatingToolbarContainer(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        scrollBehavior = scrollBehavior,
        content = {
            ToolbarIconButton(
                icon = Icons.Rounded.ArrowUpward,
                label = "بالا",
                onClick = { scope.launch { lazyListState.animateScrollToItem(0) } }
            )
            ToolbarIconButton(
                icon = Icons.Rounded.Info,
                label = "راهنما",
                onClick = onInfoClick
            )
            ToolbarIconButton(
                icon = Icons.Rounded.Image,
                label = "نقشه",
                onClick = onMapClick
            )
        },
        fab = null
    )
}