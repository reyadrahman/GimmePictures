package ua.andrii.andrushchenko.gimmepictures.data.auth

import retrofit2.http.POST
import retrofit2.http.Query

interface AuthorizationService {

    @POST("oauth/token")
    suspend fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("code") code: String,
        @Query("grant_type") grantType: String
    ): AccessToken
}