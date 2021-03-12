package ua.andrii.andrushchenko.gimmepictures.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun hasWritePermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
            context.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

fun Context.hasPermission(vararg permissions: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        permissions.all { singlePermission ->
            ContextCompat.checkSelfPermission(this,
                singlePermission) == PackageManager.PERMISSION_GRANTED
        }
    else true
}

fun Fragment.requestPermission(vararg permissions: String, @IntRange(from = 0) requestCode: Int) =
    requestPermissions(permissions, requestCode)