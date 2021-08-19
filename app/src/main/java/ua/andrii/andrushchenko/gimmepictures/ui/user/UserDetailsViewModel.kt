package ua.andrii.andrushchenko.gimmepictures.ui.user

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.user.UserRepository
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import ua.andrii.andrushchenko.gimmepictures.domain.User
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _error: MutableLiveData<Boolean> = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> get() = _user

    fun setUser(user: User) {
        _user.postValue(user)
    }

    fun refreshUserProfile() {
        _user.postValue(_user.value)
    }

    fun getUserProfile(username: String) = viewModelScope.launch {
        when (val result = userRepository.getUserPublicProfile(username)) {
            is BackendResult.Success -> {
                _error.postValue(false)
                setUser(result.value)
            }
            is BackendResult.Error -> {
                _error.postValue(true)
            }
            else -> {
                _error.postValue(false)
            }
        }
    }

    val userPhotos: LiveData<PagingData<Photo>> = _user.switchMap {
        userRepository.getUserPhotos(it.username ?: "").cachedIn(viewModelScope)
    }

    val userLikedPhotos: LiveData<PagingData<Photo>> = _user.switchMap {
        userRepository.getUserLikedPhotos(it.username ?: "").cachedIn(viewModelScope)
    }

    val userCollections: LiveData<PagingData<Collection>> = _user.switchMap {
        userRepository.getUserCollections(it.username ?: "").cachedIn(viewModelScope)
    }
}