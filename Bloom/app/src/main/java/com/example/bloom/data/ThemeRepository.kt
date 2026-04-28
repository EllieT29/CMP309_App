package com.example.bloom.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

//Repository for theme
class ThemeRepository(context: Context) {

    //Shared preferences for storing theme
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences ("theme_prefs", Context.MODE_PRIVATE)

    //Key used to store dark theme preference
    private val DARK_THEME = "dark_theme"

    //Save dark theme preference to shared preferences
    fun saveTheme(isDarkTheme: Boolean)  {
        sharedPreferences.edit { putBoolean(DARK_THEME, isDarkTheme) }

    }

    //Get dark theme preference from shared preferences
    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME, false)
    }
}