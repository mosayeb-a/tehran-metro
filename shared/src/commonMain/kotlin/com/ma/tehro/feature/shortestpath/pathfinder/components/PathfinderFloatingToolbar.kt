package com.ma.tehro.feature.shortestpath.pathfinder.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ma.tehro.common.ui.FloatingToolbarContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PathfinderFloatingToolbar(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    onInfoClick: () -> Unit,
    onMapClick: () -> Unit,
    scrollBehavior: Any? = null,
) {
    val scope = rememberCoroutineScope()

    FloatingToolbarContainer(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        scrollBehavior = scrollBehavior,
        content = {
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = { scope.launch { lazyListState.animateScrollToItem(0) } }
            ) {
                Icon(Icons.Filled.ArrowUpward, contentDescription = "Scroll to top", tint = Color.White)
            }
            Spacer(Modifier.width(4.dp))
            IconButton(modifier = Modifier.size(48.dp), onClick = onInfoClick) {
                Icon(Icons.Filled.Info, contentDescription = "Info", tint = Color.White)
            }
            Spacer(Modifier.width(4.dp))
            IconButton(modifier = Modifier.size(48.dp), onClick = onMapClick) {
                Icon(Icons.Filled.Image, contentDescription = "Metro map", tint = Color.White)
            }
        },
        fab = null
    )
}