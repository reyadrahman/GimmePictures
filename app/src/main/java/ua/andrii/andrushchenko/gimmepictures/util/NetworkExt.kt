package ua.andrii.andrushchenko.gimmepictures.util

import retrofit2.HttpException

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Error(val code: Int? = null, val error: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    object NetworkError : Result<Nothing>()
}

val HttpException.errorBody: String?
    get() = try {
        this.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        null
    }