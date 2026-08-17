package com.example.pilloramoney.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DeepDarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    primaryContainer = PrimaryOrange.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryOrange,
    secondary = TextSecondary,
    onSecondary = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = BorderColor,
    outlineVariant = BorderColor.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    primaryContainer = PrimaryOrange.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryOrange,
    secondary = TextSecondaryLight,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CardBackgroundLight,
    onSurfaceVariant = TextSecondaryLight,
    error = ErrorRed,
    onError = Color.White,
    outline = BorderColorLight,
    outlineVariant = BorderColorLight.copy(alpha = 0.5f)
)

@Composable
fun PilloraMoneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DeepDarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
