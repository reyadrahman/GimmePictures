package ua.andrii.andrushchenko.gimmepictures.ui.photo

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotoRepository
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotosPagingSource
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _order = MutableLiveData(PhotosPagingSource.Companion.Order.LATEST)
    val order get() = _order

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

    var listStateParcel: Parcelable? = null
}

