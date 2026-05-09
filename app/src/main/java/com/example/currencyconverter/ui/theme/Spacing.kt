package com.example.currencyconverter.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Tokens mirror DESIGN_SYSTEM.md §4. Material3 has no spacing scale, so we expose
// it via a CompositionLocal: read via `LocalSpacing.current.md` inside composables.

data class Spacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
