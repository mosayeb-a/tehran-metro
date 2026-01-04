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
    val hasFab = fab != null

    HorizontalFloatingToolbar(
        expanded = true,
        floatingActionButton = if (hasFab) {
            {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = {},
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    fab()
                }
            }
        } else {
            { }
        },
        modifier = modifier
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
            .offset(y = -FloatingToolbarDefaults.ScreenOffset),
        colors = if (hasFab) {
            FloatingToolbarDefaults.vibrantFloatingToolbarColors(
                toolbarContainerColor = containerColor,
                toolbarContentColor = contentColor
            )
        } else {
            FloatingToolbarDefaults.standardFloatingToolbarColors(
                toolbarContainerColor = containerColor,
                toolbarContentColor = contentColor
            )
        },
        expandedShadowElevation = if (hasFab) 0.dp else 4.dp,
        content = content,
        scrollBehavior = actualScrollBehavior
    )
}