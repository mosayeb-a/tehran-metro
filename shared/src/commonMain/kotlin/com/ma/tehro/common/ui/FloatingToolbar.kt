package com.ma.tehro.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FloatingToolbarContainer(
    modifier: Modifier,
    containerColor: Color,
    contentColor: Color,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
    content: @Composable RowScope.() -> Unit,
    fab: @Composable (() -> Unit)?,
) {
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
            scrollBehavior = scrollBehavior
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
            scrollBehavior = scrollBehavior
        )
    }
}

@Composable
fun ToolbarIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
) {
    IconButton(
        modifier = modifier.width(54.dp).height(56.dp),
        onClick = onClick
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = tint
            )
        }
    }
}