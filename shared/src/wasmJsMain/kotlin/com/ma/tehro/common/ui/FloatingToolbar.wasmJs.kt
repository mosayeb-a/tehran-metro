package com.ma.tehro.common.ui


import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun FloatingToolbarContainer(
    modifier: Modifier,
    containerColor: Color,
    contentColor: Color,
    scrollBehavior: Any?,
    content: @Composable RowScope.() -> Unit,
    fab: @Composable (() -> Unit)?,

    ) {
    BottomAppBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        actions = content,
        floatingActionButton = fab
    )
}