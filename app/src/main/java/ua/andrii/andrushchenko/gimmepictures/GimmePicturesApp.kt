package ua.andrii.andrushchenko.gimmepictures

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ua.andrii.andrushchenko.gimmepictures.util.ThemeHelper
import javax.inject.Inject

@HiltAndroidApp
class GimmePicturesApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = prefs.getString("theme", ThemeHelper.DEFAULT_MODE)
        ThemeHelper.applyTheme(themePref!!)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        GlideApp.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        GlideApp.get(this).trimMemory(level)
    }
}