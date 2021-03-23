package ua.andrii.andrushchenko.gimmepictures.ui.auth

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotoRepository
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import javax.inject.Inject
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.util.ApiCallResult

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    val loginUrl = authRepository.loginUrl
    private val _backgroundPhoto by lazy {
        val liveData = MutableLiveData<Photo>()
        viewModelScope.launch {
            val result = photoRepository.getRandomPhoto(featured = true)
            if (result is ApiCallResult.Success) liveData.value = result.value
        }
        return@lazy liveData
    }

    val backgroundPhoto: LiveData<Photo> get() = _backgroundPhoto

    fun getAccessToken(code: String) = liveData(viewModelScope.coroutineContext) {
        emit(ApiCallResult.Loading)
        val accessTokenResult = authRepository.getAccessToken(code)
        emit(accessTokenResult)
    }
}