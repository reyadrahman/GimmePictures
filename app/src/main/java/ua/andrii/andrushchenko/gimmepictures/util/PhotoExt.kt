package ua.andrii.andrushchenko.gimmepictures.util

import ua.andrii.andrushchenko.gimmepictures.models.Photo
import java.util.*

enum class PhotoQuality {
    RAW, FULL, REGULAR, SMALL, THUMB
}

fun getPhotoUrl(photo: Photo, quality: PhotoQuality?): String {
    return when (quality) {
        PhotoQuality.RAW -> photo.urls.raw
        PhotoQuality.FULL -> photo.urls.full
        PhotoQuality.REGULAR -> photo.urls.regular
        PhotoQuality.SMALL -> photo.urls.small
        PhotoQuality.THUMB -> photo.urls.thumb
        else -> photo.urls.regular
    }
}

val Photo.fileName: String
    get() = "${
        this.user?.name?.toLowerCase(Locale.ROOT)?.replace(" ", "_")
    }_${this.id}_unsplash.jpg"