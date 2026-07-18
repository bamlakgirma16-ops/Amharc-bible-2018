package com.example.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Available App Themes
enum class AppTheme(val displayName: String) {
    SYSTEM("System Default"),
    LIGHT("Classic Light"),
    DARK("Slate Dark"),
    COSMIC_DARK("Cosmic Night")
}

// Available Accent Colors with their corresponding color values
enum class AccentColor(
    val displayName: String,
    val lightPrimary: Color,
    val lightSecondary: Color,
    val darkPrimary: Color,
    val darkSecondary: Color
) {
    VIOLET(
        displayName = "Royal Violet",
        lightPrimary = Color(0xFF6750A4),
        lightSecondary = Color(0xFF625B71),
        darkPrimary = Color(0xFFD0BCFF),
        darkSecondary = Color(0xFFCCC2DC)
    ),
    EMERALD(
        displayName = "Emerald Mint",
        lightPrimary = Color(0xFF006C4C),
        lightSecondary = Color(0xFF4C6357),
        darkPrimary = Color(0xFF6EDB9E),
        darkSecondary = Color(0xFFB3CCBE)
    ),
    AMBER(
        displayName = "Amber Sunset",
        lightPrimary = Color(0xFF825500),
        lightSecondary = Color(0xFF705D49),
        darkPrimary = Color(0xFFFFB951),
        darkSecondary = Color(0xFFDDC2A1)
    ),
    ROSE(
        displayName = "Ruby Rose",
        lightPrimary = Color(0xFF9C4146),
        lightSecondary = Color(0xFF775657),
        darkPrimary = Color(0xFFFFB3B5),
        darkSecondary = Color(0xFFE6BDBC)
    ),
    CYAN(
        displayName = "Ocean Cyan",
        lightPrimary = Color(0xFF006874),
        lightSecondary = Color(0xFF4A6267),
        darkPrimary = Color(0xFF80D2E1),
        darkSecondary = Color(0xFFB1CBD0)
    )
}

// Data class representing the global application preferences state
data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val accentColor: AccentColor = AccentColor.VIOLET,
    val fontSizeMultiplier: Float = 1.0f,
    val notificationsEnabled: Boolean = true,
    val showOnboarding: Boolean = true,
    val username: String = "Alex Developer"
)

/**
 * State holder that encapsulates the observable state of user preferences.
 * This acts as the "Context Value" object in React.
 */
class PreferencesStateHolder(
    initialPreferences: UserPreferences,
    private val onUpdate: (UserPreferences) -> Unit
) {
    var preferences by mutableStateOf(initialPreferences)
        private set

    // Method to update preferences and save them through the callback
    fun updatePreferences(newPrefs: UserPreferences) {
        preferences = newPrefs
        onUpdate(newPrefs)
    }

    // Sugar helpers for updating individual fields
    fun setTheme(theme: AppTheme) {
        updatePreferences(preferences.copy(theme = theme))
    }

    fun setAccentColor(accentColor: AccentColor) {
        updatePreferences(preferences.copy(accentColor = accentColor))
    }

    fun setFontSizeMultiplier(multiplier: Float) {
        updatePreferences(preferences.copy(fontSizeMultiplier = multiplier))
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        updatePreferences(preferences.copy(notificationsEnabled = enabled))
    }

    fun setShowOnboarding(show: Boolean) {
        updatePreferences(preferences.copy(showOnboarding = show))
    }

    fun setUsername(name: String) {
        updatePreferences(preferences.copy(username = name))
    }
}

/**
 * The CompositionLocal equivalent of React.createContext().
 * It provides a staticCompositionLocalOf for state holder binding.
 */
val LocalUserPreferences = staticCompositionLocalOf<PreferencesStateHolder> {
    error("No PreferencesStateHolder provided! Wrap your content in a CompositionLocalProvider.")
}

/**
 * Convenient accessor function, analogous to custom React hook `usePreferences()` or `useContext(PreferencesContext)`.
 */
val usePreferences: PreferencesStateHolder
    @Composable
    @ReadOnlyComposable
    get() = LocalUserPreferences.current
