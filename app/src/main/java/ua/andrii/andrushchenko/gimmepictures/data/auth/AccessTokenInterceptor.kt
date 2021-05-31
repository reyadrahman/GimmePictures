package ua.andrii.andrushchenko.gimmepictures.data.auth

import okhttp3.Interceptor
import okhttp3.Response
import ua.andrii.andrushchenko.gimmepictures.data.common.CLIENT_ID

class AccessTokenInterceptor(private val accessTokenProvider: AccessTokenProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (accessTokenProvider.isAuthorized) {
            val token = accessTokenProvider.accessToken
            val authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            val authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Client-ID $CLIENT_ID")
                .build()
            chain.proceed(authenticatedRequest)
        }
    }
}