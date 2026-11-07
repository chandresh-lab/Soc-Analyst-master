package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Default SOC Master Dark Theme (Black + Gold + Dark Blue)
private val SocDarkColorScheme = darkColorScheme(
    primary = SocGoldPrimary,
    onPrimary = Color(0xFF0F0B00),
    primaryContainer = SocGoldContainer,
    onPrimaryContainer = SocGoldLight,
    secondary = SocBlueAccent,
    onSecondary = Color(0xFF021B32),
    secondaryContainer = SocBlueDeep,
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = SocTerminalGreen,
    onTertiary = Color(0xFF003822),
    background = SocDarkBlueBg,
    onBackground = SocTextPrimary,
    surface = SocDarkBlueSurface,
    onSurface = SocTextPrimary,
    surfaceVariant = SocDarkBlueSurfaceElevated,
    onSurfaceVariant = SocTextSecondary,
    outline = SocBlueBorder,
    error = SocAlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // SOC Operations Center is strictly dark-mode themed
    dynamicColor: Boolean = false, // Keep bespoke Black + Gold + Dark Blue branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SocDarkColorScheme,
        typography = Typography,
        content = content
    )
}
