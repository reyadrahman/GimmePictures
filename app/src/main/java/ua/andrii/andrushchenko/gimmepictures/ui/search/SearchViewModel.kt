package ua.andrii.andrushchenko.gimmepictures.ui.search

import androidx.lifecycle.*
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.search.SearchPhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.data.search.SearchRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _query: MutableLiveData<String> = MutableLiveData()
    val query: LiveData<String> get() = _query

    // Photo search filter params
    var order = SearchPhotosPagingSource.Companion.Order.RELEVANT
    var contentFilter = SearchPhotosPagingSource.Companion.ContentFilter.LOW
    var color = SearchPhotosPagingSource.Companion.Color.ANY
    var orientation = SearchPhotosPagingSource.Companion.Orientation.ANY

    val photoResults = _query.switchMap { query ->
        searchRepository.searchPhotos(query, order, null, contentFilter, color, orientation)
    }.cachedIn(viewModelScope)

    val collectionResults = _query.switchMap { query ->
        searchRepository.searchCollections(query)
    }.cachedIn(viewModelScope)

    val usersResult = _query.switchMap { query ->
        searchRepository.searchUsers(query)
    }.cachedIn(viewModelScope)

    fun updateQuery(query: String) = _query.postValue(query)

    fun filterPhotoSearch() = _query.postValue(_query.value)
}