package ua.andrii.andrushchenko.gimmepictures.data.collection

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Collection

@Parcelize
data class CollectionPhotoResult(
    val photo: Photo?,
    val collection: Collection?
) : Parcelable