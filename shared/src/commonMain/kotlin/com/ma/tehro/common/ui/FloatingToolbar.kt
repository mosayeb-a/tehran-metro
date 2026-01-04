package com.ma.tehro.common.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
expect fun FloatingToolbarContainer(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    scrollBehavior: Any? = null,
    content: @Composable RowScope.() -> Unit,
    fab: @Composable (() -> Unit)? =null,
)