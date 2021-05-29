package ua.andrii.andrushchenko.gimmepictures.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.andrii.andrushchenko.gimmepictures.data.auth.AccessTokenProvider
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthorizationService
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsRepository
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsService
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotoRepository
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotoService
import ua.andrii.andrushchenko.gimmepictures.data.search.SearchRepository
import ua.andrii.andrushchenko.gimmepictures.data.search.SearchService
import ua.andrii.andrushchenko.gimmepictures.data.user.UserRepository
import ua.andrii.andrushchenko.gimmepictures.data.user.UserService
import javax.inject.Singleton

/**
 * Place provideRepos methods directly in
 * this module to keep all dependencies in one place.
 * Prefer this way to direct constructor injection.
 * Also it's more convenient for testing.
 * */

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        accessTokenProvider: AccessTokenProvider,
        authorizationService: AuthorizationService,
        userService: UserService
    ): AuthRepository = AuthRepository(accessTokenProvider, authorizationService, userService)

    @Provides
    @Singleton
    fun providePhotosRepository(photoService: PhotoService): PhotoRepository =
        PhotoRepository(photoService)

    @Provides
    @Singleton
    fun provideCollectionsRepository(
        collectionsService: CollectionsService,
        userService: UserService
    ): CollectionsRepository = CollectionsRepository(collectionsService, userService)

    @Provides
    @Singleton
    fun provideSearchRepository(searchService: SearchService): SearchRepository =
        SearchRepository(searchService)

    @Provides
    @Singleton
    fun provideUserRepository(userService: UserService): UserRepository =
        UserRepository(userService)
}