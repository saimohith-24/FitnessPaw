package com.example.fitnesspaw.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(

    primary = Color(0xFF8EA2FF),

    background = Color(0xFF020617),

    surface = Color(0xFF0F172A),

    onPrimary = Color.White,

    onBackground = Color.White,

    onSurface = Color.White
)

private val LightColors = lightColorScheme(

    primary = Color(0xFF5B6CFF),

    background = Color(0xFFF8FAFC),

    surface = Color.White,

    onPrimary = Color.White,

    onBackground = Color.Black,

    onSurface = Color.Black
)

@Composable
fun FitnessPawTheme(

    darkTheme: Boolean =
        isSystemInDarkTheme(),

    content: @Composable () -> Unit
) {

    val colors =

        if (darkTheme)
            DarkColors

        else
            LightColors

    MaterialTheme(

        colorScheme = colors,

        typography = Typography(),

        content = content
    )
}