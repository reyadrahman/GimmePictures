package ua.andrii.andrushchenko.gimmepictures.ui.settings

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.andrii.andrushchenko.gimmepictures.GlideApp
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {

    private val _glideCacheSize: MutableLiveData<Long> by lazy {
        val liveData: MutableLiveData<Long> = MutableLiveData(0L)
        liveData.postValue(getGlideCacheSize())
        return@lazy liveData
    }
    val glideCacheSize: LiveData<Long> = _glideCacheSize

    fun launchClearCache() {
        viewModelScope.launch {
            clearCache()
            _glideCacheSize.postValue(getGlideCacheSize())
        }
    }

    private suspend fun clearCache() =
        withContext(Dispatchers.Default) {
            GlideApp.get(getApplication<Application>().applicationContext).clearDiskCache()
        }

    private fun getGlideCacheSize() =
        GlideApp.getPhotoCacheDir(getApplication<Application>().applicationContext)?.dirSize()

    private fun File.dirSize(): Long {
        if (this.exists()) {
            var result: Long = 0
            listFiles()?.forEach { aFileList ->
                result += if (aFileList.isDirectory) {
                    this.dirSize()
                } else {
                    aFileList.length()
                }
            }
            return result / 1024 / 1024
        }
        return 0
    }
}