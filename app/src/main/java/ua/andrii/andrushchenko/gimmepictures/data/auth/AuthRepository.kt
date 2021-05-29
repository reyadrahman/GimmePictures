package ua.andrii.andrushchenko.gimmepictures.data.auth

import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_ID
import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_SECRET
import ua.andrii.andrushchenko.gimmepictures.data.user.UserService
import ua.andrii.andrushchenko.gimmepictures.models.Me
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.backendRequest

class AuthRepository(
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

    suspend fun getAccessToken(code: String): BackendResult<AccessToken> {
        val result = backendRequest {
            authorizationService.getAccessToken(
                CLIENT_ID,
                CLIENT_SECRET,
                redirectUri,
                code,
                grantType
            )
        }

        if (result is BackendResult.Success) {
            accessTokenProvider.saveAccessToken(result.value)
        }

        return result
    }

    suspend fun getMyProfile(): BackendResult<Me> {
        val result = backendRequest {
            userService.getUserPrivateProfile()
        }

        if (result is BackendResult.Success) {
            accessTokenProvider.saveUserProfileInfo(result.value)
        }

        return result
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
    ): BackendResult<Me> {
        val result = backendRequest {
            userService.updateUserPrivateProfile(
                username, firstName, lastName, email, url, instagramUsername, location, bio
            )
        }

        if (result is BackendResult.Success) {
            accessTokenProvider.saveUserProfileInfo(result.value)
        }

        return result
    }

    val isAuthorized: Boolean
        get() = accessTokenProvider.isAuthorized

    val userNickname: String?
        get() = accessTokenProvider.userNickname

    val userFistName: String?
        get() = accessTokenProvider.userFirstName

    val userLastName: String?
        get() = accessTokenProvider.userLastName

    val userProfilePhotoUrl: String?
        get() = accessTokenProvider.userProfilePhotoUrl

    val userEmail: String?
        get() = accessTokenProvider.userEmail

    val userPortfolioLink: String?
        get() = accessTokenProvider.userPortfolioLink

    val userInstagramUsername: String?
        get() = accessTokenProvider.userInstagramUsername

    val userLocation: String?
        get() = accessTokenProvider.userLocation

    val userBio: String?
        get() = accessTokenProvider.userBio

    fun logout() = accessTokenProvider.clear()

    companion object {
        const val unsplashAuthCallback = "unsplash-auth-callback"
        private const val redirectUri = "gmpictures://$unsplashAuthCallback"
        private const val grantType = "authorization_code"
    }
}