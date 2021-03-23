package ua.andrii.andrushchenko.gimmepictures.data.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_ID
import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_SECRET
import ua.andrii.andrushchenko.gimmepictures.data.user.UserService
import ua.andrii.andrushchenko.gimmepictures.models.Me
import ua.andrii.andrushchenko.gimmepictures.util.BackendCallResult
import ua.andrii.andrushchenko.gimmepictures.util.backendRequest
import ua.andrii.andrushchenko.gimmepictures.util.backendRequestFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val accessTokenProvider: AccessTokenProvider,
    private val authorizationService: AuthorizationService,
    private val userService: UserService,
) {
    val loginUrl: String
        get() = "https://unsplash.com/oauth/authorize" +
                "?client_id=$CLIENT_ID" +
                "&redirect_uri=gmpictures%3A%2F%2F$unsplashAuthCallback" +
                "&response_type=code" +
                "&scope=public+read_user+write_user+read_photos+write_photos" +
                "+write_likes+write_followers+read_collections+write_collections"

    suspend fun getAccessToken(code: String): BackendCallResult<AccessToken> {
        val result = backendRequest {
            authorizationService.getAccessToken(
                CLIENT_ID,
                CLIENT_SECRET,
                redirectUri,
                code,
                grantType
            )
        }

        if (result is BackendCallResult.Success) {
            accessTokenProvider.saveAccessToken(result.value)
        }

        return result
    }

    suspend fun getMyProfile(): Flow<BackendCallResult<Me>> = backendRequestFlow {
        val result: Me
        withContext(Dispatchers.IO) {
            result = userService.getUserPrivateProfile()
        }
        return@backendRequestFlow result
    }

    suspend fun updateMe(
        username: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        url: String?,
        instagramUsername: String?,
        location: String?,
        bio: String?,
    ): Flow<BackendCallResult<Me>> = backendRequestFlow {
        val result: Me
        withContext(Dispatchers.IO) {
            result = userService.updateUserPrivateProfile(
                username, firstName, lastName, email, url, instagramUsername, location, bio
            )
        }
        return@backendRequestFlow result
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