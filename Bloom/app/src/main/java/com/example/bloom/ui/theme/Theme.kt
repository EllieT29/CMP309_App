package com.example.bloom.ui.theme

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

//Set dark colour scheme
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

//Set light colour scheme
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
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    //Choose a color scheme based on the parameters
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        //If darkTheme is true, use the DarkColorScheme
        darkTheme -> DarkColorScheme
        //If darkTheme is false, use the LightColorScheme
        else -> LightColorScheme
    }

    //Apply the MaterialTheme with the chosen color scheme, typography, and content
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}