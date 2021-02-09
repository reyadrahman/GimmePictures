package ua.andrii.andrushchenko.gimmepictures.data.repositories

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import kotlinx.coroutines.Dispatchers
import ua.andrii.andrushchenko.gimmepictures.data.api.PhotoService
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.source.PhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.util.Result
import ua.andrii.andrushchenko.gimmepictures.util.safeApiRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val photoService: PhotoService
) {
    fun getAllPhotos(order: PhotosPagingSource.Companion.Order): LiveData<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotosPagingSource(photoService, order) }
        ).liveData

    suspend fun getSinglePhoto(photoId: String): Result<Photo> =
        safeApiRequest(Dispatchers.IO) {
            photoService.getPhoto(photoId)
        }
}