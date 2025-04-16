package com.ma.tehro.feature.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val AppColorScheme = darkColorScheme(
    background = DarkGray,
    primary = Gray,
    onPrimary = Color.White,
    secondary = DarkGray,
    onSecondary = Color.White,
    tertiary = Blue,
    onTertiary = Color.White
)

@Composable
fun TehroTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = AppColorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}