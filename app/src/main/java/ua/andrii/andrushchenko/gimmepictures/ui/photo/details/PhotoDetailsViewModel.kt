package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotoRepository
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.util.Result
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
) : ViewModel() {

    private val _result: MutableLiveData<Result<Photo>> = MutableLiveData()
    val result get() = _result

    fun getPhotoDetails(photoId: String) {
        viewModelScope.launch {
            photoRepository.getSinglePhoto(photoId).onEach {
                _result.postValue(it)
            }.launchIn(viewModelScope)
        }
    }
}