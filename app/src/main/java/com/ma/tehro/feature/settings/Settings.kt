package com.ma.tehro.feature.settings


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ma.tehro.common.ui.Appbar
import com.ma.tehro.common.ui.theme.AppThemes
import com.ma.tehro.feature.settings.components.AppThemeItem

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondary,
        topBar = {
            Appbar(
                fa = "تنظیمات",
                en = "Settings",
                onBackClick = onBack,
            )
        },
    ) { innerPadding ->
        LazyRow(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(AppThemes) { theme ->
                AppThemeItem(
                    title = theme.name,
                    colorScheme = theme.colorScheme,
                    amoledBlack = false,
                    darkTheme = 2,
                    selected = theme.name == currentTheme?.name,
                    onClick = { viewModel.setTheme(theme) }
                )
            }
        }
    }
}
