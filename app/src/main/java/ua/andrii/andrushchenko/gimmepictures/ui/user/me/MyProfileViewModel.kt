package ua.andrii.andrushchenko.gimmepictures.ui.user.me

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.models.Me
import ua.andrii.andrushchenko.gimmepictures.util.BackendCallResult
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isUserAuthorized = MutableLiveData(authRepository.isAuthorized)
    val isUserAuthorized get() = _isUserAuthorized

    private val _backendCallResult: MutableLiveData<BackendCallResult<Me>> = MutableLiveData()
    val apiCallResult get() = _backendCallResult

    fun obtainMyProfile() = viewModelScope.launch {
        authRepository.getMyProfile().onEach {
            _backendCallResult.postValue(it)
        }.launchIn(viewModelScope)
    }

    fun logout() {
        authRepository.logout()
        _isUserAuthorized.postValue(false)
    }
}