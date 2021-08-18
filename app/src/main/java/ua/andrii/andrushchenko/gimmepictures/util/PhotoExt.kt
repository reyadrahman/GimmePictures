package ua.andrii.andrushchenko.gimmepictures.util

import androidx.annotation.StringRes
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import java.util.*

enum class PhotoSize(@StringRes val stringId: Int) {
    RAW(R.string.image_size_raw),
    FULL(R.string.image_size_full),
    REGULAR(R.string.image_size_regular),
    SMALL(R.string.image_size_small),
    THUMB(R.string.image_size_thumb)
}

fun Photo.getUrlForSize(size: PhotoSize?): String {
    return when (size) {
        PhotoSize.RAW -> this.urls.raw
        PhotoSize.FULL -> this.urls.full
        PhotoSize.REGULAR -> this.urls.regular
        PhotoSize.SMALL -> this.urls.small
        PhotoSize.THUMB -> this.urls.thumb
        else -> this.urls.regular
    }
}

val Photo.fileName: String
    get() = "${this.id}+${
        this.user?.name?.lowercase(Locale.ROOT)?.replace(" ", "_")
    }+unsplash.jpg"