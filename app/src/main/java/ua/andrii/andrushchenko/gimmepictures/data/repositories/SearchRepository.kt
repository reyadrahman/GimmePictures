package ua.andrii.andrushchenko.gimmepictures.data.repositories

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import ua.andrii.andrushchenko.gimmepictures.data.api.SearchService
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.source.SearchPhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.models.Collection
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val searchService: SearchService
) {

    fun searchPhotos(
        query: String,
        order: SearchPhotosPagingSource.Companion.Order,
        collections: String?,
        contentFilter: SearchPhotosPagingSource.Companion.ContentFilter,
        color: SearchPhotosPagingSource.Companion.Color,
        orientation: SearchPhotosPagingSource.Companion.Orientation
    ): LiveData<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPhotosPagingSource(
                    searchService,
                    query,
                    order,
                    collections,
                    contentFilter,
                    color,
                    orientation
                )
            }
        ).liveData

    fun searchCollections(): LiveData<PagingData<Collection>> {
        TODO()
    }

    fun searchUsers(): LiveData<PagingData<User>> {
        TODO()
    }
}