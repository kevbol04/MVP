package com.example.mvp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    // Brand
    primary = Accent,
    secondary = Accent2,

    // Backgrounds / Surfaces
    background = BgTop,
    surface = BgMid,
    surfaceVariant = GlassBase.copy(alpha = 0.06f),

    // Text / Icons on surfaces
    onBackground = TextOnDark,
    onSurface = TextOnDark,
    onSurfaceVariant = TextOnDark.copy(alpha = 0.78f),

    // Text on brand colors
    onPrimary = ButtonTextDark,
    onSecondary = ButtonTextDark,

    // Borders / dividers
    outline = OutlineOnDark.copy(alpha = 0.18f),
    outlineVariant = OutlineOnDark.copy(alpha = 0.10f),

    // Semantic
    error = Danger,
    onError = ButtonTextDark
)

private val LightColorScheme = lightColorScheme(
    primary = Accent,
    secondary = Accent2,

    background = BgTop,
    surface = BgMid,
    surfaceVariant = GlassBase.copy(alpha = 0.06f),

    onBackground = TextOnDark,
    onSurface = TextOnDark,
    onSurfaceVariant = TextOnDark.copy(alpha = 0.78f),

    onPrimary = ButtonTextDark,
    onSecondary = ButtonTextDark,

    outline = OutlineOnDark.copy(alpha = 0.18f),
    outlineVariant = OutlineOnDark.copy(alpha = 0.10f),

    error = Danger,
    onError = ButtonTextDark
)

@Composable
fun MVPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}