package com.example.currencyconverter.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Tokens mirror DESIGN_SYSTEM.md §2.

val LightColors = lightColorScheme(
    primary = Color(0xFF0A6EFF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD9E4FF),
    onPrimaryContainer = Color(0xFF001A41),
    secondary = Color(0xFF565E71),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFAFAFB),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFAFAFB),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFEEF0F4),
    onSurfaceVariant = Color(0xFF44474E),
    outline = Color(0xFF74777F),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFAEC6FF),
    onPrimary = Color(0xFF002E6A),
    primaryContainer = Color(0xFF0049A6),
    onPrimaryContainer = Color(0xFFD9E4FF),
    secondary = Color(0xFFBEC6DC),
    onSecondary = Color(0xFF283041),
    background = Color(0xFF111316),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF111316),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF1D1F23),
    onSurfaceVariant = Color(0xFFC4C6CF),
    outline = Color(0xFF8E9099),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)
