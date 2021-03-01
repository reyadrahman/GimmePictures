package ua.andrii.andrushchenko.gimmepictures.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import ua.andrii.andrushchenko.gimmepictures.models.Me
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessTokenProvider @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    val accessToken: String?
        get() = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    val username: String?
        get() = sharedPreferences.getString(USERNAME_KEY, null)

    val email: String?
        get() = sharedPreferences.getString(EMAIL_KEY, null)

    val profilePicture: String?
        get() = sharedPreferences.getString(PROFILE_PICTURE_KEY, null)

    val isAuthorized: Boolean
        get() = !accessToken.isNullOrEmpty()

    fun saveAccessToken(accessToken: AccessToken) = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, accessToken.access_token)
    }

    fun saveUserProfile(me: Me) = sharedPreferences.edit {
        putString(USERNAME_KEY, me.username)
        putString(EMAIL_KEY, me.email)
        putString(PROFILE_PICTURE_KEY, me.profile_image?.large)
    }

    fun clear() = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, null)
        putString(USERNAME_KEY, null)
        putString(EMAIL_KEY, null)
        putString(PROFILE_PICTURE_KEY, null)
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val USERNAME_KEY = "user_username"
        private const val EMAIL_KEY = "user_email"
        private const val PROFILE_PICTURE_KEY = "user_profile_picture"
    }
}