package ua.andrii.andrushchenko.gimmepictures.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import ua.andrii.andrushchenko.gimmepictures.BuildConfig
import java.io.File

const val GIMME_PICTURES_DIRECTORY = "GimmePictures"

const val FILE_PROVIDER_AUTHORITY = "${BuildConfig.APPLICATION_ID}.fileprovider"

val GIMME_PICTURES_RELATIVE_PATH =
    "${Environment.DIRECTORY_PICTURES}${File.separator}${GIMME_PICTURES_DIRECTORY}"

// Keep it for SDK versions lower than 29
@Suppress("DEPRECATION")
val GIMME_PICTURES_LEGACY_PATH = "${
    Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
    )
}${File.separator}$GIMME_PICTURES_DIRECTORY"

fun Context.fileExists(fileName: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} like ? and " +
                "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf("%$GIMME_PICTURES_RELATIVE_PATH%", fileName)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        contentResolver.query(uri, projection, selection, selectionArgs, null)?.use {
            return it.count > 0
        } ?: return false
    } else {
        return File(GIMME_PICTURES_LEGACY_PATH, fileName).exists()
    }
}
