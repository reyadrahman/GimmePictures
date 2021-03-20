package ua.andrii.andrushchenko.gimmepictures.util

import android.app.Activity
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import ua.andrii.andrushchenko.gimmepictures.R

fun Activity.setTransparentStatusBar(isTransparent: Boolean) {
    if (isTransparent) {
        window.run {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            statusBarColor =
                ContextCompat.getColor(this@setTransparentStatusBar, R.color.black_half_transparent)
        }
    } else {
        window.run {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true)
            statusBarColor = typedValue.data
        }
    }
}
