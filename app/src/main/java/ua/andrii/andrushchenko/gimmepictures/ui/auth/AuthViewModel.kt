package ua.andrii.andrushchenko.gimmepictures.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    val loginUrl = authRepository.loginUrl

    fun getAccessToken(code: String) = liveData(viewModelScope.coroutineContext) {
        emit(BackendResult.Loading)
        val accessTokenResult = authRepository.getAccessToken(code)
        if (accessTokenResult is BackendResult.Success) {
            authRepository.getMyProfile()
        }
        emit(accessTokenResult)
    }
}