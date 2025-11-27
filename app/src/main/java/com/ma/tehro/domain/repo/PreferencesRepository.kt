package com.ma.tehro.domain.repo

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val selectedThemeFlow: Flow<String>
    suspend fun saveTheme(themeName: String)
}
