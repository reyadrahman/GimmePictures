package ua.andrii.andrushchenko.gimmepictures.ui.user

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.user.UserRepository
import ua.andrii.andrushchenko.gimmepictures.models.Collection
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val usernameLiveData: MutableLiveData<String> = MutableLiveData("")

    private val _error: MutableLiveData<Boolean> = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> get() = _user

    fun getUserProfile(username: String) = viewModelScope.launch {
        when (val result = userRepository.getUserProfile(username)) {
            is BackendResult.Success -> {
                _error.postValue(false)
                _user.postValue(result.value)
                usernameLiveData.postValue(username)
            }
            is BackendResult.Error -> {
                _error.postValue(true)
            }
            else -> {
                _error.postValue(false)
            }
        }
    }

    /*val userPhotos: LiveData<PagingData<Photo>> = usernameLiveData.switchMap { username ->
        userRepository.getUserPhotos(username).cachedIn(viewModelScope)
    }

    val userLikedPhotos: LiveData<PagingData<Photo>> = usernameLiveData.switchMap { username ->
        userRepository.getUserLikedPhotos(username).cachedIn(viewModelScope)
    }

    val userCollections: LiveData<PagingData<Collection>> = usernameLiveData.switchMap { username ->
        userRepository.getUserCollections(username).cachedIn(viewModelScope)
    }*/

    val userPhotos: LiveData<PagingData<Photo>> by lazy {
        val photos = userRepository.getUserPhotos(usernameLiveData.value ?: "")
        return@lazy photos.cachedIn(viewModelScope)
    }

    val userLikedPhotos: LiveData<PagingData<Photo>> by lazy {
        val likedPhotos = userRepository.getUserLikedPhotos(usernameLiveData.value ?: "")
        return@lazy likedPhotos.cachedIn(viewModelScope)
    }

    val userCollections: LiveData<PagingData<Collection>> by lazy {
        val userCollections = userRepository.getUserCollections(usernameLiveData.value ?: "")
        return@lazy userCollections.cachedIn(viewModelScope)
    }
}