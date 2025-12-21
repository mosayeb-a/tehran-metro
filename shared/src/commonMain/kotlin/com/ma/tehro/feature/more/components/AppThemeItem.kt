package com.ma.tehro.feature.more.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppThemeItem(
    title: String,
    colorScheme: ColorScheme,
    amoledBlack: Boolean,
    darkTheme: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .height( 165.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppThemePreviewItem(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            selected = selected,
            onClick = onClick,
            colorScheme = colorScheme.copy(
                background = if (amoledBlack && (darkTheme == 0 && isSystemInDarkTheme() || darkTheme == 2)) {
                    Color.Black
                } else {
                    colorScheme.background
                }
            ),
            shapes = MaterialTheme.shapes
        )
        Spacer(Modifier.height(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall
        )
    }
}