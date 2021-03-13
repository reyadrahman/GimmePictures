package ua.andrii.andrushchenko.gimmepictures.ui.user.me

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.models.Me
import javax.inject.Inject
import ua.andrii.andrushchenko.gimmepictures.util.Result

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isAuthorized get() = authRepository.isAuthorized
    val username get() = authRepository.username
    val userProfilePhotoUrl get() = authRepository.profilePicture

    private val _me: MutableLiveData<Me> = MutableLiveData()
    val me: LiveData<Me> get() = _me

    fun obtainProfile() = viewModelScope.launch {
        val result = authRepository.getMe()
        if (result is Result.Success) {
            _me.postValue(result.value)
        }
    }

    fun logout() {
        authRepository.logout()
    }

}