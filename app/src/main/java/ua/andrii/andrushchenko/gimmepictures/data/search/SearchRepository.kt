package ua.andrii.andrushchenko.gimmepictures.data.search

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import ua.andrii.andrushchenko.gimmepictures.domain.User

interface SearchRepository {

    fun searchPhotos(
        query: String,
        order: SearchPhotosPagingSource.Companion.Order,
        collections: String?,
        contentFilter: SearchPhotosPagingSource.Companion.ContentFilter,
        color: SearchPhotosPagingSource.Companion.Color,
        orientation: SearchPhotosPagingSource.Companion.Orientation,
    ): LiveData<PagingData<Photo>>

    fun searchCollections(query: String): LiveData<PagingData<Collection>>

    fun searchUsers(query: String): LiveData<PagingData<User>>

}