package ua.andrii.andrushchenko.gimmepictures.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.andrii.andrushchenko.gimmepictures.util.NotificationHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext applicationContext: Context): NotificationHelper =
        NotificationHelper(applicationContext)

}