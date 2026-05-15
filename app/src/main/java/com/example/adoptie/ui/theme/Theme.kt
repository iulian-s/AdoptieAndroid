package com.example.adoptie.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = SurfaceVariantLight,
    onPrimaryContainer = TealPrimaryDark,
    secondary = CoralAccent,
    onSecondary = Color.White,
    secondaryContainer = CoralAccentLight.copy(alpha = 0.3f),
    onSecondaryContainer = Color(0xFF5C3D35),
    tertiary = TealPrimaryLight,
    background = BackgroundLight,
    onBackground = Color(0xFF1C2423),
    surface = SurfaceLight,
    onSurface = Color(0xFF1C2423),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = Color(0xFFB8C9C7),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryLight,
    onPrimary = Color(0xFF003738),
    primaryContainer = TealPrimaryDark,
    onPrimaryContainer = Color(0xFFB8E0E0),
    secondary = CoralAccentLight,
    onSecondary = Color(0xFF442A22),
    background = BackgroundDark,
    onBackground = Color(0xFFE8EDEC),
    surface = SurfaceDark,
    onSurface = Color(0xFFE8EDEC),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB8C9C7),
    outline = Color(0xFF5C6B6A)
)

@Composable
fun AdoptieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
