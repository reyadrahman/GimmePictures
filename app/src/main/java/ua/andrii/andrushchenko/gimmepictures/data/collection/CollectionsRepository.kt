package ua.andrii.andrushchenko.gimmepictures.data.collection

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.models.Collection
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionsRepository @Inject constructor(private val collectionsService: CollectionsService) {

    fun getAllCollections(order: CollectionsPagingSource.Companion.Order): LiveData<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionsPagingSource(collectionsService, order) }
        ).liveData

    fun getCollectionPhotos(collectionId: Int): LiveData<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionPhotosPagingSource(collectionsService, collectionId) }
        ).liveData

    /*fun getSingleCollection(id: Int): Flow<ApiCallResult<Collection>> = flow {
        emit(ApiCallResult.Loading)
        try {
            val result: Collection
            withContext(Dispatchers.IO) {
                result = collectionsService.getCollection(id)
            }
            emit(ApiCallResult.Success(result))
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> emit(ApiCallResult.NetworkError)
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.errorBody
                    emit(ApiCallResult.Error(code, errorResponse))
                }
                else -> emit(ApiCallResult.Error(null, throwable.message))
            }
        }
    }*/
}