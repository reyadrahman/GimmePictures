package ua.andrii.andrushchenko.gimmepictures.data.photos

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import okhttp3.ResponseBody
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.backendRequest

class PhotoRepository(private val photoService: PhotoService) {
    fun getAllPhotos(order: PhotosPagingSource.Companion.Order): LiveData<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotosPagingSource(photoService, order) }
        ).liveData

    suspend fun getSinglePhoto(photoId: String): BackendResult<Photo> = backendRequest {
        photoService.getPhoto(photoId)
    }

    suspend fun likePhoto(id: String): BackendResult<ResponseBody> = backendRequest {
        photoService.likePhoto(id)
    }

    suspend fun dislikePhoto(id: String): BackendResult<Unit> = backendRequest {
        photoService.dislikePhoto(id)
    }
}