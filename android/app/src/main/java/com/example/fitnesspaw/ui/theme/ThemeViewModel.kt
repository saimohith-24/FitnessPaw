package com.example.fitnesspaw.ui.theme

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesspaw.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    // Expose selected theme mode: 0 = Light, 1 = Dark, 2 = System
    val themeMode = mutableIntStateOf(2)

    init {
        viewModelScope.launch {
            themeMode.intValue = userPreferences.themeModeFlow.first()
        }
    }

    fun setThemeMode(mode: Int) {
        themeMode.intValue = mode
        viewModelScope.launch {
            userPreferences.saveThemeMode(mode)
        }
    }
}