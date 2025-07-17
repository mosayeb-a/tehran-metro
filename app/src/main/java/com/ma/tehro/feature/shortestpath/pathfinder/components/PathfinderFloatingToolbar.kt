package com.ma.tehro.feature.shortestpath.pathfinder.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.FloatingToolbarScrollBehavior
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PathfinderFloatingToolbar(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    onInfoClick: () -> Unit,
    onImageClick: () -> Unit,
    scrollBehavior: FloatingToolbarScrollBehavior
) {
    val coroutineScope = rememberCoroutineScope()

    HorizontalFloatingToolbar(
        modifier = modifier
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            )
            .offset(y = -ScreenOffset),
        expanded = true,
        colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
            toolbarContainerColor = MaterialTheme.colorScheme.primary,
            toolbarContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        expandedShadowElevation = 4.dp,
        content = {
            FilledIconButton(
                modifier = Modifier.size(48.dp),
                onClick = {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
            ) {
                Icon(
                    Icons.Filled.ArrowUpward,
                    contentDescription = "Scroll to top",
                )
            }
            Spacer(Modifier.width(4.dp))
            FilledIconButton(
                modifier = Modifier.size(48.dp),
                onClick = onInfoClick,
            ) {
                Icon(Icons.Filled.Info, contentDescription = "Info")
            }
            Spacer(Modifier.width(4.dp))
            FilledIconButton(
                modifier = Modifier.size(48.dp),
                onClick = onImageClick,
            ) {
                Icon(Icons.Filled.Image, contentDescription = "Metro image")
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
