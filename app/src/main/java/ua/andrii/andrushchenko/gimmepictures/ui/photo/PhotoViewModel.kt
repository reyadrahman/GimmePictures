package ua.andrii.andrushchenko.gimmepictures.ui.photo

import androidx.lifecycle.*
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotosRepository
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val photoRepository: PhotosRepository
) : ViewModel() {

    private val _order = MutableLiveData(PhotosPagingSource.Companion.Order.LATEST)
    val order: LiveData<PhotosPagingSource.Companion.Order> get() = _order

    val photos = order.switchMap { order ->
        photoRepository.getAllPhotos(order).cachedIn(viewModelScope)
    }

    val isAuthorized: Boolean
        get() = authRepository.isAuthorized

    fun orderPhotosBy(selection: Int) {
        val orderBy = when (selection) {
            0 -> PhotosPagingSource.Companion.Order.LATEST
            1 -> PhotosPagingSource.Companion.Order.OLDEST
            else -> PhotosPagingSource.Companion.Order.POPULAR
        }
        _order.postValue(orderBy)
    }
}

