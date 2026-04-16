package com.example.bloom

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ThemeRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences ("theme_prefs", Context.MODE_PRIVATE)

    private val DARK_THEME = "dark_theme"

    fun saveTheme(isDarkTheme: Boolean)  {
        sharedPreferences.edit { putBoolean(DARK_THEME, isDarkTheme) }

    }

    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME, false)
    }
}