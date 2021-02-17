package ua.andrii.andrushchenko.gimmepictures.util

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import ua.andrii.andrushchenko.gimmepictures.R

fun AppCompatActivity.setTransparentStatusBar(isTransparent: Boolean) {
    val regularStatusBarColor = window.statusBarColor
    if (isTransparent) {
        window.run {
            WindowCompat.setDecorFitsSystemWindows(this, false)
            statusBarColor =
                ContextCompat.getColor(this@setTransparentStatusBar, R.color.black_half_transparent)
        }
    } else {
        window.run {
            WindowCompat.setDecorFitsSystemWindows(this, true)
            statusBarColor = regularStatusBarColor
        }
    }
}
