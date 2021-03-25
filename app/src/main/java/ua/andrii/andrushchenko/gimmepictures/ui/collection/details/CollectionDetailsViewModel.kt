package ua.andrii.andrushchenko.gimmepictures.ui.collection.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsRepository
import javax.inject.Inject

@HiltViewModel
class CollectionDetailsViewModel @Inject constructor(
    private val collectionsRepository: CollectionsRepository,
) : ViewModel() {

    private val _collectionPhotos: MutableLiveData<Int> = MutableLiveData()

    val collectionPhotos = _collectionPhotos.switchMap { collectionId ->
        collectionsRepository.getCollectionPhotos(collectionId).cachedIn(viewModelScope)
    }

    fun getCollectionPhotos(collectionId: Int) {
        _collectionPhotos.postValue(collectionId)
    }
}