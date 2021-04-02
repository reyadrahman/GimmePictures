package ua.andrii.andrushchenko.gimmepictures.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

sealed class BackendResult<out T> {
    object Loading : BackendResult<Nothing>()
    data class Success<out T>(val value: T) : BackendResult<T>()
    data class Error(val code: Int? = null, val error: String? = null) : BackendResult<Nothing>()
}

suspend fun <T> backendRequest(request: suspend () -> T): BackendResult<T> =
    withContext(Dispatchers.IO) {
        try {
            BackendResult.Success(request.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.errorBody
                    BackendResult.Error(code, errorResponse)
                }
                else -> BackendResult.Error(null, throwable.message)
            }
        }
    }

/*suspend fun <T> backendRequestFlow(fetchData: suspend () -> T): Flow<BackendCallResult<T>> = flow {
    emit(BackendCallResult.Loading)
    try {
        val result = fetchData.invoke()
        emit(BackendCallResult.Success(result))
    } catch (throwable: Throwable) {
        when (throwable) {
            is HttpException -> {
                val code = throwable.code()
                val errorResponse = throwable.errorBody
                emit(BackendCallResult.Error(code, errorResponse))
            }
            else -> emit(BackendCallResult.Error(null, throwable.message))
        }
    }
}*/

val HttpException.errorBody: String?
    get() = try {
        this.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        null
    }
