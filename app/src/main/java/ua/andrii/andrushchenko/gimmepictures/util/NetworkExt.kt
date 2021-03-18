package ua.andrii.andrushchenko.gimmepictures.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

sealed class ApiCallResult<out T> {
    data class Success<out T>(val value: T) : ApiCallResult<T>()
    data class Error(val code: Int? = null, val error: String? = null) : ApiCallResult<Nothing>()
    object NetworkError : ApiCallResult<Nothing>()
    object Loading : ApiCallResult<Nothing>()
}

suspend fun <T> safeApiRequest(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    request: suspend () -> T,
): ApiCallResult<T> {
    return withContext(dispatcher) {
        try {
            ApiCallResult.Success(request.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ApiCallResult.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.errorBody
                    ApiCallResult.Error(code, errorResponse)
                }
                else -> ApiCallResult.Error(null, throwable.message)
            }
        }
    }
}

val HttpException.errorBody: String?
    get() = try {
        this.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        null
    }
