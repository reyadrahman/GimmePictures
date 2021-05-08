package ua.andrii.andrushchenko.gimmepictures.data.photos

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.backendRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(private val photoService: PhotoService) {
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

    suspend fun getRandomPhoto(
        collectionId: Int? = null,
        featured: Boolean = false,
        username: String? = null,
        query: String? = null,
        orientation: String? = null,
        contentFilter: String? = null
    ): BackendResult<Photo> = backendRequest {
        photoService.getRandomPhotos(collectionId,
            featured,
            username,
            query,
            orientation,
            contentFilter,
            count = 1).first()
    }

    suspend fun likePhoto(id: String) = backendRequest { photoService.likePhoto(id) }

    suspend fun dislikePhoto(id: String) = backendRequest { photoService.dislikePhoto(id) }
}