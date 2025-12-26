package com.ma.tehro.common.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.ma.tehro.domain.Theme

private val BlueColorScheme = darkColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    secondary = Gray,
    onSecondary = Color.White,
    background = DarkGray,
    onBackground = Color.White,
    primaryContainer = lerp(Gray, Blue, 0.25f),
    onPrimaryContainer = Color.White,
)

private val MochaColorScheme = darkColorScheme(
    primary = Color(0xFFF2EDE6),
    onPrimary = Color.Black,
    secondary = Color(0xFF4D463D),
    onSecondary = Color.White,
    background = Color(0xFF23211F),
    onBackground = Color(0xFFF2EDE6),
    primaryContainer = lerp(Color(0xFF23211F), Color(0xFF4D463D), .5f),
    onPrimaryContainer = Color.White,
)

private val ForestColorScheme = darkColorScheme(
    primary = Color(0xFF9FD1B7),
    onPrimary = Color.Black,
    secondary = Color(0xFF354F52),
    onSecondary = Color.White,
    background = Color(0xFF2F3E46),
    onBackground = Color(0xFFE0E0E0),
    primaryContainer = lerp(Color(0xFF2F3E46), Color(0xFF81B29A), 0.4f),
    onPrimaryContainer = Color.White,
)

private val OceanColorScheme = darkColorScheme(
    primary = Color(0xFF78B2E6),
    onPrimary = Color.Black,
    secondary = Color(0xFF356C7A),
    onSecondary = Color.White,
    background = Color(0xFF1F2D33),
    onBackground = Color(0xFFE3F2FD),
    primaryContainer = lerp(Color(0xFF1F2D33), Color(0xFF64B5F6), 0.3f),
    onPrimaryContainer = Color.White,
)

val Themes = listOf(
    Theme("آبی", BlueColorScheme),
    Theme("موکا", MochaColorScheme),
    Theme("جنگل", ForestColorScheme),
    Theme("اقیانوس", OceanColorScheme)
)

@Composable
fun TehroTheme(
    colorScheme: ColorScheme = BlueColorScheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}