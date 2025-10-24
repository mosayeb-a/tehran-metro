package com.ma.tehro.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PreferencesRepository {
    val selectedThemeFlow: Flow<String>
    suspend fun saveTheme(themeName: String)
}

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {
    companion object {
        private val THEME_KEY = stringPreferencesKey("selected_theme")
    }

    override val selectedThemeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "Blue"
    }

    override suspend fun saveTheme(themeName: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeName
        }
    }
}