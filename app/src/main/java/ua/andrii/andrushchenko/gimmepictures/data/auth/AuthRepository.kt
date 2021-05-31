package ua.andrii.andrushchenko.gimmepictures.data.auth

import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_ID
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Me
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult

interface AuthRepository {

    val loginUrl: String
        get() = "https://unsplash.com/oauth/authorize" +
                "?client_id=$CLIENT_ID" +
                "&redirect_uri=gmpictures%3A%2F%2F${AuthRepositoryImpl.unsplashAuthCallback}" +
                "&response_type=code" +
                "&scope=public+read_user+write_user+read_photos+write_photos" +
                "+write_likes+write_followers+read_collections+write_collections"

    suspend fun getAccessToken(code: String): BackendResult<AccessToken>

    suspend fun getMyProfile(): BackendResult<Me>

    suspend fun updateMe(
        username: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        url: String?,
        instagramUsername: String?,
        location: String?,
        bio: String?
    ): BackendResult<Me>

    val isAuthorized: Boolean

    val userNickname: String?

    val userFistName: String?

    val userLastName: String?

    val userProfilePhotoUrl: String?

    val userEmail: String?

    val userPortfolioLink: String?

    val userInstagramUsername: String?

    val userLocation: String?

    val userBio: String?

    fun logout()

}