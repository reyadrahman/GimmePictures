package ua.andrii.andrushchenko.gimmepictures.util

import android.app.Activity
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
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

fun Activity.transparentStatusBar(isTransparent: Boolean, isFullscreen: Boolean) {
    if (isTransparent) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    } else {
        if (isFullscreen) {
            val decorView: View = window.decorView
            val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        or View.SYSTEM_UI_FLAG_VISIBLE)
            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}
