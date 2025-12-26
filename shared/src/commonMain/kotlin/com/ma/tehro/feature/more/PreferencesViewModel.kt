package com.ma.tehro.feature.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma.tehro.common.ui.theme.Themes
import com.ma.tehro.domain.Theme
import com.ma.tehro.domain.repo.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _currentTheme = MutableStateFlow<Theme?>(null)
    val currentTheme: StateFlow<Theme?> = _currentTheme

    init {
        viewModelScope.launch {
            preferencesRepository.selectedThemeFlow.collect { savedThemeName ->
                val theme = Themes.find { it.name == savedThemeName } ?: Themes[0]
                _currentTheme.value = theme
            }
        }
    }

    fun setTheme(theme: Theme) {
        _currentTheme.value = theme
        viewModelScope.launch {
            preferencesRepository.saveTheme(theme.name)
        }
    }
}