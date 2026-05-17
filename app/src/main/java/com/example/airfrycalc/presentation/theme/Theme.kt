package com.example.airfrycalc.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = AirFryOrange,
    secondary = AirFryYellow,
    background = AirFryDark,
    surface = AirFrySurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = AirFryOnDark,
    onSurface = AirFryOnDark,
    primaryContainer = Color(0xFF3D2000),
    onPrimaryContainer = AirFryYellow,
    secondaryContainer = Color(0xFF3D3000),
    onSecondaryContainer = AirFryYellow,
    error = Color(0xFFCF6679),
    tertiary = StepComplete,
    outline = Color(0xFF555555)
)

@Composable
fun AirFryCalcTheme(content: @Composable () -> Unit) {
    val currentDensity = LocalDensity.current
    val cappedDensity = Density(
        density = currentDensity.density,
        fontScale = currentDensity.fontScale.coerceAtMost(1.2f)
    )
    CompositionLocalProvider(
        LocalDensity provides cappedDensity,
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
