package ua.andrii.andrushchenko.gimmepictures.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

sealed class BackendCallResult<out T> {
    object Loading : BackendCallResult<Nothing>()
    data class Success<out T>(val value: T) : BackendCallResult<T>()
    data class Error(val code: Int? = null, val error: String? = null) : BackendCallResult<Nothing>()
}

suspend fun <T> backendRequest(request: suspend () -> T): BackendCallResult<T> =
    withContext(Dispatchers.IO) {
        try {
            BackendCallResult.Success(request.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.errorBody
                    BackendCallResult.Error(code, errorResponse)
                }
                else -> BackendCallResult.Error(null, throwable.message)
            }
        }
    }

suspend fun <T> backendRequestFlow(fetchData: suspend () -> T): Flow<BackendCallResult<T>> = flow {
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
}

val HttpException.errorBody: String?
    get() = try {
        this.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        null
    }
