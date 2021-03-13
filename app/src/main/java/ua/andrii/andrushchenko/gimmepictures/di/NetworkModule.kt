package ua.andrii.andrushchenko.gimmepictures.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.andrii.andrushchenko.gimmepictures.data.auth.AccessTokenInterceptor
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthorizationService
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsService
import ua.andrii.andrushchenko.gimmepictures.data.common.BASE_API_URL
import ua.andrii.andrushchenko.gimmepictures.data.common.BASE_URL
import ua.andrii.andrushchenko.gimmepictures.data.download.DownloadService
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotoService
import ua.andrii.andrushchenko.gimmepictures.data.search.SearchService
import ua.andrii.andrushchenko.gimmepictures.data.user.UserService
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(accessTokenInterceptor: AccessTokenInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(accessTokenInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @Named("api_url_retrofit")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("auth_url_retrofit")
    fun provideAuthRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providePhotoService(@Named("api_url_retrofit") retrofit: Retrofit): PhotoService =
        retrofit.create(PhotoService::class.java)

    @Provides
    @Singleton
    fun provideCollectionService(@Named("api_url_retrofit") retrofit: Retrofit): CollectionsService =
        retrofit.create(CollectionsService::class.java)

    @Provides
    @Singleton
    fun provideSearchService(@Named("api_url_retrofit") retrofit: Retrofit): SearchService =
        retrofit.create(SearchService::class.java)

    @Provides
    @Singleton
    fun provideUserService(@Named("api_url_retrofit") retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideDownloadService(@Named("api_url_retrofit") retrofit: Retrofit): DownloadService =
        retrofit.create(DownloadService::class.java)

    @Provides
    @Singleton
    fun provideAuthorizationService(@Named("auth_url_retrofit") retrofit: Retrofit): AuthorizationService =
        retrofit.create(AuthorizationService::class.java)
}