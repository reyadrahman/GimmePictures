package ua.andrii.andrushchenko.gimmepictures.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.search.SearchRepository
import ua.andrii.andrushchenko.gimmepictures.data.search.SearchPhotosPagingSource
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _query = savedStateHandle.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)
    val query get() = _query

    var order = SearchPhotosPagingSource.Companion.Order.RELEVANT
    var contentFilter = SearchPhotosPagingSource.Companion.ContentFilter.LOW
    var color = SearchPhotosPagingSource.Companion.Color.ANY
    var orientation = SearchPhotosPagingSource.Companion.Orientation.ANY

    val photoResults = _query.switchMap { query ->
        searchRepository.searchPhotos(query, order, null, contentFilter, color, orientation)
    }.cachedIn(viewModelScope)

    fun updateQuery(query: String) = _query.postValue(query)

    fun filterPhotoSearch() = _query.postValue(_query.value)

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = ""
    }

}