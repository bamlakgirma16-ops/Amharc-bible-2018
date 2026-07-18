package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.preferences.AccentColor
import com.example.preferences.AppTheme
import com.example.preferences.LocalUserPreferences

// Base color fallbacks
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// Cosmic Night Color Scheme (Custom high-fidelity deep space theme)
private val CosmicDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5C4FF),
    onPrimary = Color(0xFF002F66),
    primaryContainer = Color(0xFF00448F),
    onPrimaryContainer = Color(0xFFD6E2FF),
    secondary = Color(0xFFBCC6DC),
    onSecondary = Color(0xFF263147),
    background = Color(0xFF0C1322),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF141F35),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF23304A),
    onSurfaceVariant = Color(0xFFC3C6CF),
    outline = Color(0xFF8D9199)
)

/**
 * Utility to scale a TextStyle dynamically using the multiplier
 */
private fun scaleStyle(style: TextStyle, multiplier: Float): TextStyle {
    return if (style.fontSize.isSp) {
        val originalSize = style.fontSize.value
        val originalLineHeight = if (style.lineHeight.isSp) style.lineHeight.value else originalSize * 1.35f
        style.copy(
            fontSize = (originalSize * multiplier).sp,
            lineHeight = (originalLineHeight * multiplier).sp
        )
    } else {
        style
    }
}

/**
 * Generates typography scaled by the preferred multiplier.
 */
fun getScaledTypography(multiplier: Float): Typography {
    return Typography(
        bodyLarge = scaleStyle(Typography.bodyLarge, multiplier),
        bodyMedium = scaleStyle(Typography.bodyMedium, multiplier),
        bodySmall = scaleStyle(Typography.bodySmall, multiplier),
        titleLarge = scaleStyle(Typography.titleLarge, multiplier),
        titleMedium = scaleStyle(Typography.titleMedium, multiplier),
        titleSmall = scaleStyle(Typography.titleSmall, multiplier),
        headlineLarge = scaleStyle(Typography.headlineLarge, multiplier),
        headlineMedium = scaleStyle(Typography.headlineMedium, multiplier),
        headlineSmall = scaleStyle(Typography.headlineSmall, multiplier),
        labelLarge = scaleStyle(Typography.labelLarge, multiplier),
        labelMedium = scaleStyle(Typography.labelMedium, multiplier),
        labelSmall = scaleStyle(Typography.labelSmall, multiplier),
        displayLarge = scaleStyle(Typography.displayLarge, multiplier),
        displayMedium = scaleStyle(Typography.displayMedium, multiplier),
        displaySmall = scaleStyle(Typography.displaySmall, multiplier)
    )
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    // 1. Read global preferences from the CompositionLocal (identical to React useContext)
    val preferencesState = LocalUserPreferences.current
    val currentPrefs = preferencesState.preferences

    // 2. Determine light/dark mode based on setting or system default
    val isDark = when (currentPrefs.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.COSMIC_DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    // 3. Resolve the Dynamic Color Scheme
    val accent = currentPrefs.accentColor
    val colorScheme = when {
        // Special theme background overrides
        currentPrefs.theme == AppTheme.COSMIC_DARK -> CosmicDarkColorScheme

        // Normal Theme - dynamic accent-driven palette
        isDark -> {
            darkColorScheme(
                primary = accent.darkPrimary,
                secondary = accent.darkSecondary,
                tertiary = Pink80,
                background = Color(0xFF121318),
                surface = Color(0xFF1A1B22)
            )
        }
        else -> {
            lightColorScheme(
                primary = accent.lightPrimary,
                secondary = accent.lightSecondary,
                tertiary = Pink40,
                background = Color(0xFFFAF9FD),
                surface = Color(0xFFFFFFFF)
            )
        }
    }

    // 4. Resolve the Scaled Typography
    val scaledTypography = getScaledTypography(currentPrefs.fontSizeMultiplier)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )
}

