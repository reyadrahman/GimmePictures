package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotosRepository
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val photoRepository: PhotosRepository
) : ViewModel() {

    // Look inside the onViewCreated in PhotoDetailsFragment.kt for description
    var isDataInitialized = false

    val isUserAuthorized get() = authRepository.isAuthorized

    private val _authorizedUserNickName: MutableLiveData<String> =
        MutableLiveData(authRepository.userNickname)

    private val _photo: MutableLiveData<Photo> = MutableLiveData()
    val photo: LiveData<Photo> get() = _photo

    private val _error: MutableLiveData<Boolean> = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

    private val _currentUserCollectionIds = MutableLiveData<MutableList<String>?>()
    val currentUserCollectionIds: LiveData<MutableList<String>?> = _currentUserCollectionIds

    fun getPhotoDetails(photoId: String) = viewModelScope.launch {
        when (val result = photoRepository.getSinglePhoto(photoId)) {
            is BackendResult.Success -> {
                _photo.postValue(result.value)
                _currentUserCollectionIds.postValue(
                    result.value.currentUserCollections?.map { it.id }?.toMutableList()
                )
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

    fun likePhoto(id: String) = viewModelScope.launch { photoRepository.likePhoto(id) }

    fun dislikePhoto(id: String) = viewModelScope.launch { photoRepository.dislikePhoto(id) }

    fun notifyUserAuthorizationSuccessful() {
        _authorizedUserNickName.postValue(authRepository.userNickname)
    }

    fun updateCurrentUserCollectionIds(currentUserCollectionIds: MutableList<String>?) {
        _currentUserCollectionIds.postValue(currentUserCollectionIds)
    }
}