package com.example.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "app_user_preferences",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_THEME = "pref_theme"
        private const val KEY_ACCENT = "pref_accent"
        private const val KEY_FONT_SIZE = "pref_font_size"
        private const val KEY_NOTIFS = "pref_notifications"
        private const val KEY_ONBOARDING = "pref_onboarding"
        private const val KEY_USERNAME = "pref_username"
    }

    /**
     * Loads the saved preferences or returns defaults if none exist.
     */
    fun loadPreferences(): UserPreferences {
        val themeString = sharedPrefs.getString(KEY_THEME, null)
        val accentString = sharedPrefs.getString(KEY_ACCENT, null)
        val fontSize = sharedPrefs.getFloat(KEY_FONT_SIZE, 1.0f)
        val notifsEnabled = sharedPrefs.getBoolean(KEY_NOTIFS, true)
        val showOnboarding = sharedPrefs.getBoolean(KEY_ONBOARDING, true)
        val username = sharedPrefs.getString(KEY_USERNAME, "Alex Developer") ?: "Alex Developer"

        val theme = try {
            if (themeString != null) AppTheme.valueOf(themeString) else AppTheme.SYSTEM
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }

        val accent = try {
            if (accentString != null) AccentColor.valueOf(accentString) else AccentColor.VIOLET
        } catch (e: Exception) {
            AccentColor.VIOLET
        }

        return UserPreferences(
            theme = theme,
            accentColor = accent,
            fontSizeMultiplier = fontSize,
            notificationsEnabled = notifsEnabled,
            showOnboarding = showOnboarding,
            username = username
        )
    }

    /**
     * Saves the entire preferences object to SharedPreferences.
     */
    fun savePreferences(prefs: UserPreferences) {
        sharedPrefs.edit().apply {
            putString(KEY_THEME, prefs.theme.name)
            putString(KEY_ACCENT, prefs.accentColor.name)
            putFloat(KEY_FONT_SIZE, prefs.fontSizeMultiplier)
            putBoolean(KEY_NOTIFS, prefs.notificationsEnabled)
            putBoolean(KEY_ONBOARDING, prefs.showOnboarding)
            putString(KEY_USERNAME, prefs.username)
            apply()
        }
    }
}
