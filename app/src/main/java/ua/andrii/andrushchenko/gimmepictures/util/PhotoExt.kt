package ua.andrii.andrushchenko.gimmepictures.util

import androidx.annotation.StringRes
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import java.util.*

enum class PhotoSize(@StringRes val stringId: Int) {
    ORIGINAL(R.string.image_size_original),
    LARGE(R.string.image_size_large),
    MEDIUM(R.string.image_size_medium),
    SMALL(R.string.image_size_small),
    THUMB(R.string.image_size_thumb)
}

fun Photo.getUrlForSize(size: PhotoSize?): String = when (size) {
    PhotoSize.ORIGINAL -> this.urls.raw
    PhotoSize.LARGE -> this.urls.full
    PhotoSize.MEDIUM -> this.urls.regular
    PhotoSize.SMALL -> this.urls.small
    PhotoSize.THUMB -> this.urls.thumb
    else -> this.urls.raw
}

val Photo.fileName: String
    get() = "${this.id}+${
        this.user?.name?.lowercase(Locale.ROOT)?.replace(" ", "_")
    }+unsplash.jpg"