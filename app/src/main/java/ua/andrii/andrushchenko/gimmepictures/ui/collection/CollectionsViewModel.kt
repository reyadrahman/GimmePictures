package ua.andrii.andrushchenko.gimmepictures.ui.collection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsRepository
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(collectionsRepository: CollectionsRepository) : ViewModel() {
    val collections = collectionsRepository.getCollections().cachedIn(viewModelScope)
}