package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotoRepository
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _photo: MutableLiveData<Photo> = MutableLiveData()
    val photo get() = _photo

    private val _error: MutableLiveData<Boolean> = MutableLiveData(false)
    val error get() = _error

    fun getPhotoDetails(photoId: String) = viewModelScope.launch {
        when (val result = photoRepository.getSinglePhoto(photoId)) {
            is BackendResult.Success -> {
                _photo.postValue(result.value)
                _error.postValue(false)
            }
            is BackendResult.Error -> {
                _error.postValue(true)
            }
            else -> {
                _error.postValue(false)
            }
        }
    }

    val isUserAuthorized get() = authRepository.isAuthorized

    fun likePhoto(id: String) = viewModelScope.launch { photoRepository.likePhoto(id) }

    fun unlikePhoto(id: String) = viewModelScope.launch { photoRepository.unlikePhoto(id) }

    var downloadWorkUUID: UUID? = null
}