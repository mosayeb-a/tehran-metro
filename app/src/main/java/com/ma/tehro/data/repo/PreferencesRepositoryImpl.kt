package com.ma.tehro.data.repo

import com.ma.tehro.domain.repo.PreferencesRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.flow.Flow
import com.russhwolf.settings.coroutines.FlowSettings

@OptIn(ExperimentalSettingsApi::class)
class PreferencesRepositoryImpl(
    private val settings: FlowSettings
) : PreferencesRepository {
    companion object {
        private const val THEME_KEY = "selected_theme"
        private const val DEFAULT_THEME = "Blue"
    }

    override val selectedThemeFlow: Flow<String> = settings
        .getStringFlow(THEME_KEY, DEFAULT_THEME)

    override suspend fun saveTheme(themeName: String) {
        settings.putString(THEME_KEY, themeName)
    }
}