package ua.andrii.andrushchenko.gimmepictures.util

import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import ua.andrii.andrushchenko.gimmepictures.R

fun AppCompatActivity.setTransparentStatusBar(isTransparent: Boolean) {
    if (isTransparent) {
        window.run {
            WindowCompat.setDecorFitsSystemWindows(this, false)
            statusBarColor =
                ContextCompat.getColor(this@setTransparentStatusBar, R.color.black_half_transparent)
        }
    } else {
        window.run {
            WindowCompat.setDecorFitsSystemWindows(this, true)
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true)
            statusBarColor = typedValue.data
        }
    }
}
