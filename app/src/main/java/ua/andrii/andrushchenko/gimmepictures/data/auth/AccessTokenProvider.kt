package ua.andrii.andrushchenko.gimmepictures.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessTokenProvider @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    val accessToken: String?
        get() = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    val isAuthorized: Boolean
        get() = !accessToken.isNullOrEmpty()

    fun saveAccessToken(accessToken: AccessToken) =
        sharedPreferences.edit { putString(ACCESS_TOKEN_KEY, accessToken.access_token) }

    fun clear() = sharedPreferences.edit { putString(ACCESS_TOKEN_KEY, null) }

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
    }
}