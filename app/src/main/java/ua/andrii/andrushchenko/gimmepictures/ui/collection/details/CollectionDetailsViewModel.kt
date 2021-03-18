package ua.andrii.andrushchenko.gimmepictures.ui.collection.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsRepository
import javax.inject.Inject

@HiltViewModel
class CollectionDetailsViewModel @Inject constructor(
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    fun getCollectionPhotos(collectionId: Int) =
        collectionsRepository.getCollectionPhotos(collectionId).cachedIn(viewModelScope)

}