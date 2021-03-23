package ua.andrii.andrushchenko.gimmepictures.data.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_ID
import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_SECRET
import ua.andrii.andrushchenko.gimmepictures.data.user.UserService
import ua.andrii.andrushchenko.gimmepictures.models.Me
import ua.andrii.andrushchenko.gimmepictures.util.ApiCallResult
import ua.andrii.andrushchenko.gimmepictures.util.errorBody
import ua.andrii.andrushchenko.gimmepictures.util.safeApiRequest
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val accessTokenProvider: AccessTokenProvider,
    private val authorizationService: AuthorizationService,
    private val userService: UserService
) {
    val loginUrl: String
        get() = "https://unsplash.com/oauth/authorize" +
                "?client_id=$CLIENT_ID" +
                "&redirect_uri=gmpictures%3A%2F%2F$unsplashAuthCallback" +
                "&response_type=code" +
                "&scope=public+read_user+write_user+read_photos+write_photos" +
                "+write_likes+write_followers+read_collections+write_collections"

    suspend fun getAccessToken(code: String): ApiCallResult<AccessToken> {
        val result = safeApiRequest {
            authorizationService.getAccessToken(
                CLIENT_ID,
                CLIENT_SECRET,
                redirectUri,
                code,
                grantType
            )
        }

        if (result is ApiCallResult.Success) {
            accessTokenProvider.saveAccessToken(result.value)
        }

        return result
    }

    suspend fun getMyProfile(): Flow<ApiCallResult<Me>> = flow {
        emit(ApiCallResult.Loading)
        try {
            val result: Me
            withContext(Dispatchers.IO) {
                result = userService.getUserPrivateProfile()
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
    }

    suspend fun updateMe(
        username: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        url: String?,
        instagramUsername: String?,
        location: String?,
        bio: String?
    ): Flow<ApiCallResult<Me>> = flow {
        emit(ApiCallResult.Loading)
        try {
            val result: Me
            withContext(Dispatchers.IO) {
                result = userService.updateUserPrivateProfile(
                    username, firstName, lastName, email, url, instagramUsername, location, bio
                )
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
    }

    val isAuthorized: Boolean
        get() = accessTokenProvider.isAuthorized

    fun logout() = accessTokenProvider.clear()

    companion object {
        const val unsplashAuthCallback = "unsplash-auth-callback"
        private const val redirectUri = "gmpictures://$unsplashAuthCallback"
        private const val grantType = "authorization_code"
    }
}