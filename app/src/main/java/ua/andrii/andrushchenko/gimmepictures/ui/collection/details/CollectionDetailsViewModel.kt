package ua.andrii.andrushchenko.gimmepictures.ui.collection.details

import androidx.lifecycle.*
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsRepository
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import javax.inject.Inject

@HiltViewModel
class CollectionDetailsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    private val _collection: MutableLiveData<Collection> = MutableLiveData()
    val collection: LiveData<Collection> get() = _collection

    private val _collectionId: MutableLiveData<String> = MutableLiveData()

    // When collection has been successfully deleted
    // the collection details screen should no longer be present on the display.
    // Observe this property to implement target logic
    private val _isDeleted: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDeleted: LiveData<Boolean> get() = _isDeleted

    fun setCollection(collection: Collection) {
        _collection.postValue(collection)
        _collectionId.postValue(collection.id)
    }

    val collectionPhotos = _collectionId.switchMap { id ->
        collectionsRepository.getCollectionPhotos(id).cachedIn(viewModelScope)
    }

    fun updateCollection(id: String, title: String?, description: String?, isPrivate: Boolean) =
        viewModelScope.launch {
            val result = collectionsRepository.updateCollection(id, title, description, isPrivate)
            if (result is BackendResult.Success) {
                _collection.postValue(result.value)
            }
        }

    fun deleteCollection(id: String) = viewModelScope.launch {
        val result = collectionsRepository.deleteCollection(id)
        if (result is BackendResult.Success) {
            _isDeleted.postValue(true)
        }
    }

    val isUserAuthorized: Boolean
        get() = authRepository.isAuthorized

    val isOwnCollection: Boolean
        get() = authRepository.userNickname == _collection.value?.user?.username

}