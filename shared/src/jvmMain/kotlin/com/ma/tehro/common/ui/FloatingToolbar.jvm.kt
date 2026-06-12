package com.ma.tehro.common.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun FloatingToolbarContainer(
    modifier: Modifier,
    containerColor: Color,
    contentColor: Color,
    scrollBehavior: FloatingToolbarScrollBehavior?,
    content: @Composable (RowScope.() -> Unit),
    fab: @Composable (() -> Unit)?
) {
}