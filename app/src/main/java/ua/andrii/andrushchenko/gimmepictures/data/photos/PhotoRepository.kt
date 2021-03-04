package ua.andrii.andrushchenko.gimmepictures.data.photos

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.util.Result
import ua.andrii.andrushchenko.gimmepictures.util.errorBody
import ua.andrii.andrushchenko.gimmepictures.util.safeApiRequest
import java.io.IOException
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

    suspend fun getSinglePhoto(photoId: String): Flow<Result<Photo>> = flow {
        emit(Result.Loading)
        try {
            val result: Photo
            withContext(Dispatchers.IO) {
                result = photoService.getPhoto(photoId)
            }
            emit(Result.Success(result))
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> emit(Result.NetworkError)
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.errorBody
                    emit(Result.Error(code, errorResponse))
                }
                else -> emit(Result.Error(null, throwable.message))
            }
        }
    }

    suspend fun getRandomPhoto(
        collectionId: Int? = null,
        featured: Boolean = false,
        username: String? = null,
        query: String? = null,
        orientation: String? = null,
        contentFilter: String? = null,
    ): Result<Photo> = safeApiRequest {
        photoService.getRandomPhotos(collectionId,
            featured,
            username,
            query,
            orientation,
            contentFilter,
            count = 1).first()
    }

    suspend fun likePhoto(id: String) = safeApiRequest { photoService.likePhoto(id) }

    suspend fun unlikePhoto(id: String) = safeApiRequest { photoService.unlikePhoto(id) }
}