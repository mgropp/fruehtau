package io.gropp.fruehtau.ui

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

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors =
        when {
            dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
            dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
            darkTheme -> DarkColors
            else -> LightColors
        }

    MaterialTheme(colorScheme = colors, content = content)
}

private val LightColors =
    lightColorScheme(
        primary = Color(0xFF8C8CFF),
        onPrimary = Color.Black,
        secondary = Color(0xFF4CAF50),
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE1E1E1),
        surfaceBright = Color(0xFFFFFFFF),
        onSurface = Color(0xFF000000),
    )

private val DarkColors =
    darkColorScheme(
        primary = Color(0xFF2D2D70),
        onPrimary = Color.White,
        secondary = Color(0xFF81C784),
        background = Color(0xFF282828),
        surface = Color(0xFF303030),
        surfaceVariant = Color(0xFF3C3C3C),
        surfaceBright = Color(0xFF4F4F4F),
        onSurface = Color(0xFFFFFFFF),
    )
