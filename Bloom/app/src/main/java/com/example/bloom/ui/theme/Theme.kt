package com.example.bloom.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LightGrass,
    secondary = DarkPink,
    tertiary = DarkLavender,
    surface = DarkSage,

    onSurface = LightGrass,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,

    background = Grey,
    onBackground = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Grass,
    secondary = BabyPink,
    tertiary = Lavender,
    surface = Sage,

    onSurface = Grass,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,

    background = OffWhite,
    onBackground = Color.Black,
)

@Composable
fun BloomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}