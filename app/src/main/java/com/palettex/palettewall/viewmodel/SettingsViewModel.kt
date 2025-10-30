package com.palettex.palettewall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.palettex.palettewall.data.UserSettings
import com.palettex.palettewall.data.WallpaperDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDao = WallpaperDatabase.getDatabase(application).userSettingsDao()

    private val _isDarkThemeEnabled = MutableStateFlow(true)
    val isDarkThemeEnabled: StateFlow<Boolean> = _isDarkThemeEnabled.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsDao.isDarkThemeEnabled().collect { isDark ->
                _isDarkThemeEnabled.value = isDark ?: true
            }
        }
    }

    fun toggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsDao.updateSettings(
                UserSettings(isDarkThemeEnabled = enabled)
            )
            _isDarkThemeEnabled.value = enabled
        }
    }
}