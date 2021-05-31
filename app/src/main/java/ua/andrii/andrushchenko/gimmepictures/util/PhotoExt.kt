package ua.andrii.andrushchenko.gimmepictures.util

import androidx.annotation.StringRes
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo
import java.util.*

enum class PhotoSize(@StringRes val stringId: Int) {
    RAW(R.string.image_size_raw),
    FULL(R.string.image_size_full),
    REGULAR(R.string.image_size_regular),
    SMALL(R.string.image_size_small),
    THUMB(R.string.image_size_thumb)
}

fun getPhotoUrl(photo: Photo, size: PhotoSize?): String {
    return when (size) {
        PhotoSize.RAW -> photo.urls.raw
        PhotoSize.FULL -> photo.urls.full
        PhotoSize.REGULAR -> photo.urls.regular
        PhotoSize.SMALL -> photo.urls.small
        PhotoSize.THUMB -> photo.urls.thumb
        else -> photo.urls.regular
    }
}

val Photo.fileName: String
    get() = "${this.id}+${
        this.user?.name?.lowercase(Locale.ROOT)?.replace(" ", "_")
    }+unsplash.jpg"