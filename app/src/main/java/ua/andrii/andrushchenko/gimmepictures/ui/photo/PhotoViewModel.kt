package ua.andrii.andrushchenko.gimmepictures.ui.photo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.repositories.PhotoRepository
import ua.andrii.andrushchenko.gimmepictures.data.source.PhotosPagingSource
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _order = MutableLiveData(PhotosPagingSource.Companion.Order.LATEST)
    val order get() = _order

    val photos = order.switchMap { order ->
        photoRepository.getAllPhotos(order).cachedIn(viewModelScope)
    }

    fun orderPhotosBy(selection: Int) {
        val orderBy = when (selection) {
            0 -> PhotosPagingSource.Companion.Order.LATEST
            1 -> PhotosPagingSource.Companion.Order.OLDEST
            else -> PhotosPagingSource.Companion.Order.POPULAR
        }
        _order.postValue(orderBy)
    }
}

