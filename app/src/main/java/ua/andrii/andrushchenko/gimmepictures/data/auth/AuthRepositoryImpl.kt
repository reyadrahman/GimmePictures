package ua.andrii.andrushchenko.gimmepictures.data.auth

import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_ID
import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_SECRET
import ua.andrii.andrushchenko.gimmepictures.data.user.UserService
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Me
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.backendRequest

class AuthRepositoryImpl(
    private val accessTokenProvider: AccessTokenProvider,
    private val authorizationService: AuthorizationService,
    private val userService: UserService
) : AuthRepository {

    override suspend fun getAccessToken(code: String): BackendResult<AccessToken> {
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

    override suspend fun getMyProfile(): BackendResult<Me> {
        val result = backendRequest {
            userService.getUserPrivateProfile()
        }

        if (result is BackendResult.Success) {
            accessTokenProvider.saveUserProfileInfo(result.value)
        }

        return result
    }

    override suspend fun updateMe(
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

    override val isAuthorized: Boolean
        get() = accessTokenProvider.isAuthorized

    override val userNickname: String?
        get() = accessTokenProvider.userNickname

    override val userFistName: String?
        get() = accessTokenProvider.userFirstName

    override val userLastName: String?
        get() = accessTokenProvider.userLastName

    override val userProfilePhotoUrl: String?
        get() = accessTokenProvider.userProfilePhotoUrl

    override val userEmail: String?
        get() = accessTokenProvider.userEmail

    override val userPortfolioLink: String?
        get() = accessTokenProvider.userPortfolioLink

    override val userInstagramUsername: String?
        get() = accessTokenProvider.userInstagramUsername

    override val userLocation: String?
        get() = accessTokenProvider.userLocation

    override val userBio: String?
        get() = accessTokenProvider.userBio

    override fun logout() = accessTokenProvider.clear()

    companion object {
        const val unsplashAuthCallback = "unsplash-auth-callback"
        private const val redirectUri = "gmpictures://$unsplashAuthCallback"
        private const val grantType = "authorization_code"
    }
}