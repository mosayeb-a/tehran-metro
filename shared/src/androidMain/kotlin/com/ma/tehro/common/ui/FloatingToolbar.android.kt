package com.ma.tehro.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun FloatingToolbarContainer(
    modifier: Modifier,
    containerColor: Color,
    contentColor: Color,
    scrollBehavior: Any?,
    content: @Composable RowScope.() -> Unit,
    fab: @Composable (() -> Unit)?,
) {
    val actualScrollBehavior = scrollBehavior as? FloatingToolbarScrollBehavior
    if (fab != null) {
        HorizontalFloatingToolbar(
            expanded = true,
            floatingActionButton = {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = {},
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    fab.invoke()
                }
            },
            modifier = modifier
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
                .offset(y = -FloatingToolbarDefaults.ScreenOffset),
            colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(
                toolbarContainerColor = containerColor,
                toolbarContentColor = contentColor
            ),
            expandedShadowElevation = 0.dp,
            content = content,
            scrollBehavior = actualScrollBehavior
        )
    } else {
        HorizontalFloatingToolbar(
            expanded = true,
            modifier = modifier
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
                .offset(y = -FloatingToolbarDefaults.ScreenOffset),
            colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                toolbarContainerColor = containerColor,
                toolbarContentColor = contentColor
            ),
            expandedShadowElevation = 4.dp,
            content = content,
            scrollBehavior = actualScrollBehavior
        )
    }
}