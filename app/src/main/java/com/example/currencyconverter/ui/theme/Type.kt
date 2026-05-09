package com.example.currencyconverter.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Tokens mirror DESIGN_SYSTEM.md §3. System default font (Roboto on Android).

private val Default = FontFamily.Default

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = Default, fontWeight = FontWeight.Normal, fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium = TextStyle(fontFamily = Default, fontWeight = FontWeight.Normal, fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall = TextStyle(fontFamily = Default, fontWeight = FontWeight.Normal, fontSize = 36.sp, lineHeight = 44.sp),
    headlineMedium = TextStyle(fontFamily = Default, fontWeight = FontWeight.Normal, fontSize = 28.sp, lineHeight = 36.sp),
    titleLarge = TextStyle(fontFamily = Default, fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = Default, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = Default, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = Default, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = Default, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
)
