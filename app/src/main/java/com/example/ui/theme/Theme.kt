package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.preferences.AppTheme
import com.example.preferences.LocalUserPreferences

private val DarkColorScheme = darkColorScheme(
    primary = AnnaGoldLight,
    onPrimary = AnnaNavy,
    primaryContainer = AnnaBlue,
    onPrimaryContainer = Color.White,
    secondary = AnnaEmeraldLight,
    onSecondary = AnnaNavy,
    tertiary = AnnaGold,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = AnnaBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = AnnaNavy,
    secondary = AnnaEmerald,
    onSecondary = Color.White,
    tertiary = AnnaGold,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val preferencesState = LocalUserPreferences.current
    val currentPrefs = preferencesState.preferences

    val isDark = when (currentPrefs.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.COSMIC_DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
