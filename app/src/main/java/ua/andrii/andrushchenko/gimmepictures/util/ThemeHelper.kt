package ua.andrii.andrushchenko.gimmepictures.util

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    private const val LIGHT_MODE = "light"
    private const val DARK_MODE = "dark"
    const val DEFAULT_MODE = "system_default"

    fun applyTheme(theme: String) {
        val mode = when (theme) {
            LIGHT_MODE -> AppCompatDelegate.MODE_NIGHT_NO
            DARK_MODE -> AppCompatDelegate.MODE_NIGHT_YES
            else -> {
                when {
                    isAtLeastP() -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> AppCompatDelegate.MODE_NIGHT_NO
                }
            }
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun isAtLeastP() = Build.VERSION.SDK_INT >= 28
}