package ua.andrii.andrushchenko.gimmepictures.data.photos

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import okhttp3.ResponseBody
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult

interface PhotosRepository {

    fun getAllPhotos(order: PhotosPagingSource.Companion.Order): LiveData<PagingData<Photo>>

    suspend fun getSinglePhoto(photoId: String): BackendResult<Photo>

    suspend fun likePhoto(id: String): BackendResult<ResponseBody>

    suspend fun dislikePhoto(id: String): BackendResult<Unit>
}