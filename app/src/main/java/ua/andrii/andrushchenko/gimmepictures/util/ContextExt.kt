package ua.andrii.andrushchenko.gimmepictures.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.hasWritePermission(context: Context = this.requireContext()): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
            context.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

fun Context.hasPermission(vararg permissions: String): Boolean =
    permissions.all { singlePermission ->
        ContextCompat.checkSelfPermission(this,
            singlePermission) == PackageManager.PERMISSION_GRANTED
    }

fun Activity.requestPermission(vararg permissions: String, @IntRange(from = 0) requestCode: Int) {
    requestPermissions(permissions, requestCode)
}

fun Fragment.requestPermission(vararg permissions: String, @IntRange(from = 0) requestCode: Int) {
    requireActivity().requestPermission(*permissions, requestCode = requestCode)
}
