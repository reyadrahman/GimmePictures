package ua.andrii.andrushchenko.gimmepictures.ui.collection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsPagingSource
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsRepository
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    private val _order = MutableLiveData(CollectionsPagingSource.Companion.Order.ALL)
    val order get() = _order

    val collections = order.switchMap { order ->
        collectionsRepository.getAllCollections(order).cachedIn(viewModelScope)
    }

    fun orderCollectionsBy(selection: Int) {
        val orderBy = when (selection) {
            0 -> CollectionsPagingSource.Companion.Order.ALL
            1 -> CollectionsPagingSource.Companion.Order.FEATURED
            else -> CollectionsPagingSource.Companion.Order.ALL
        }
        _order.postValue(orderBy)
    }

}