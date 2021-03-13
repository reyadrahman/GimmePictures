package ua.andrii.andrushchenko.gimmepictures.data.collection

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
import ua.andrii.andrushchenko.gimmepictures.models.Collection
import ua.andrii.andrushchenko.gimmepictures.util.Result
import ua.andrii.andrushchenko.gimmepictures.util.errorBody
import java.io.IOException
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

    fun getSingleCollection(id: Int): Flow<Result<Collection>> = flow {
        emit(Result.Loading)
        try {
            val result: Collection
            withContext(Dispatchers.IO) {
                result = collectionsService.getCollection(id)
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
}