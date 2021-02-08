package ua.andrii.andrushchenko.gimmepictures.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.andrii.andrushchenko.gimmepictures.data.api.PhotoService
import ua.andrii.andrushchenko.gimmepictures.data.repositories.PhotoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun providePhotoRepository(photoService: PhotoService): PhotoRepository =
        PhotoRepository(photoService)
}