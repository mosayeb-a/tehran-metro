package com.ma.tehro.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.ui.theme.AppThemes
import com.ma.tehro.data.repo.SettingsRepository
import com.ma.tehro.domain.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _currentTheme = MutableStateFlow<AppTheme?>(null)
    val currentTheme: StateFlow<AppTheme?> = _currentTheme

    init {
        viewModelScope.launch {
            settingsRepository.selectedThemeFlow.collect { savedThemeName ->
                val theme = AppThemes.find { it.name == savedThemeName } ?: AppThemes[0]
                _currentTheme.value = theme
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
        viewModelScope.launch {
            settingsRepository.saveTheme(theme.name)
        }
    }
}