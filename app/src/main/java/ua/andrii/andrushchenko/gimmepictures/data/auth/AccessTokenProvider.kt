package ua.andrii.andrushchenko.gimmepictures.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import ua.andrii.andrushchenko.gimmepictures.domain.Me

class AccessTokenProvider(context: Context) {
    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    val accessToken: String?
        get() = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    val isAuthorized: Boolean
        get() = !accessToken.isNullOrEmpty()

    val userNickname: String?
        get() = sharedPreferences.getString(USER_NICKNAME_KEY, null)

    val userFirstName: String?
        get() = sharedPreferences.getString(USER_FIRST_NAME_KEY, null)

    val userLastName: String?
        get() = sharedPreferences.getString(USER_LAST_NAME_KEY, null)

    val userProfilePhotoUrl: String?
        get() = sharedPreferences.getString(USER_PROFILE_PHOTO_URL_KEY, null)

    val userEmail: String?
        get() = sharedPreferences.getString(USER_EMAIL_KEY, null)

    val userPortfolioLink: String?
        get() = sharedPreferences.getString(USER_PORTFOLIO_LINK_KEY, null)

    val userInstagramUsername: String?
        get() = sharedPreferences.getString(USER_INSTAGRAM_USERNAME_KEY, null)

    val userLocation: String?
        get() = sharedPreferences.getString(USER_LOCATION_KEY, null)

    val userBio: String?
        get() = sharedPreferences.getString(USER_BIO_KEY, null)

    fun saveAccessToken(accessToken: AccessToken) =
        sharedPreferences.edit { putString(ACCESS_TOKEN_KEY, accessToken.accessToken) }

    fun saveUserProfileInfo(me: Me) {
        sharedPreferences.edit {
            putString(USER_NICKNAME_KEY, me.username)
            putString(USER_FIRST_NAME_KEY, me.firstName)
            putString(USER_LAST_NAME_KEY, me.lastName)
            putString(USER_PROFILE_PHOTO_URL_KEY, me.profileImage?.medium)
            putString(USER_EMAIL_KEY, me.email)
            putString(USER_PORTFOLIO_LINK_KEY, me.portfolioUrl)
            putString(USER_INSTAGRAM_USERNAME_KEY, me.instagramUsername)
            putString(USER_LOCATION_KEY, me.location)
            putString(USER_BIO_KEY, me.bio)
        }
    }

    fun clear() = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, null)
        putString(USER_NICKNAME_KEY, null)
        putString(USER_FIRST_NAME_KEY, null)
        putString(USER_LAST_NAME_KEY, null)
        putString(USER_PROFILE_PHOTO_URL_KEY, null)
        putString(USER_EMAIL_KEY, null)
        putString(USER_PORTFOLIO_LINK_KEY, null)
        putString(USER_INSTAGRAM_USERNAME_KEY, null)
        putString(USER_LOCATION_KEY, null)
        putString(USER_BIO_KEY, null)
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val USER_NICKNAME_KEY = "user_nickname"
        private const val USER_FIRST_NAME_KEY = "user_first_name"
        private const val USER_LAST_NAME_KEY = "user_last_name"
        private const val USER_PROFILE_PHOTO_URL_KEY = "user_profile_photo_url"
        private const val USER_EMAIL_KEY = "user_email"
        private const val USER_PORTFOLIO_LINK_KEY = "user_portfolio_link"
        private const val USER_INSTAGRAM_USERNAME_KEY = "user_instagram_username"
        private const val USER_LOCATION_KEY = "user_location"
        private const val USER_BIO_KEY = "user_bio"
    }
}