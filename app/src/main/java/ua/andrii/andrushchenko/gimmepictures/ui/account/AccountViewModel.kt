package ua.andrii.andrushchenko.gimmepictures.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isUserAuthorized = MutableLiveData(authRepository.isAuthorized)
    val isUserAuthorized: LiveData<Boolean> get() = _isUserAuthorized

    val userNickname: String?
        get() = authRepository.userNickname

    val userFirstName: String?
        get() = authRepository.userFistName

    val userLastName: String?
        get() = authRepository.userLastName

    val userProfilePhotoUrl: String?
        get() = authRepository.userProfilePhotoUrl

    val userEmail: String?
        get() = authRepository.userEmail

    val userPortfolioLink: String?
        get() = authRepository.userPortfolioLink

    val userInstagramUsername: String?
        get() = authRepository.userInstagramUsername

    val userLocation: String?
        get() = authRepository.userLocation

    val userBio: String?
        get() = authRepository.userBio

    fun notifyUserAuthorizationSuccessful() {
        _isUserAuthorized.postValue(true)
    }

    fun updateMyProfile(
        username: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        url: String?,
        instagramUsername: String?,
        location: String?,
        bio: String?
    ) = viewModelScope.launch {
        authRepository.updateMe(
            username,
            firstName,
            lastName,
            email,
            url,
            instagramUsername,
            location,
            bio
        )
    }

    fun logout() {
        authRepository.logout()
        _isUserAuthorized.postValue(false)
    }
}