package ua.andrii.andrushchenko.gimmepictures.ui.user.me

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val username get() = authRepository.username
    val isAuthorized get() = authRepository.isAuthorized

}