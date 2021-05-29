package ua.andrii.andrushchenko.gimmepictures.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.andrii.andrushchenko.gimmepictures.data.auth.AccessTokenProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppPreferencesModule {

    @Provides
    @Singleton
    fun provideAccessTokenProvider(@ApplicationContext context: Context): AccessTokenProvider =
        AccessTokenProvider(context)

}