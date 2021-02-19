package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.repositories.PhotoRepository
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.util.Result
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _photoDetails = MutableLiveData<Photo>()
    val photoDetails get() = _photoDetails

    fun getPhotoDetails(photoId: String) {
        viewModelScope.launch {
            when (val result = photoRepository.getSinglePhoto(photoId)) {
                is Result.Success -> {
                    _photoDetails.postValue(result.value)
                }
                else -> {

                }
            }
        }
    }
}