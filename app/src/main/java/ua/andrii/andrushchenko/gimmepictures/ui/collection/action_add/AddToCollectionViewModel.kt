package ua.andrii.andrushchenko.gimmepictures.ui.collection.action_add

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionPhotoResult
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsRepository
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import javax.inject.Inject

@HiltViewModel
class AddToCollectionViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    private val authorizedUserNickname = authRepository.userNickname!!

    private val _currentUserCollectionIds = MutableLiveData<MutableList<String>?>()
    val currentUserCollectionIds: LiveData<MutableList<String>?> = _currentUserCollectionIds

    val userCollections: LiveData<PagingData<Collection>> =
        collectionsRepository.getUserCollections(authorizedUserNickname).cachedIn(viewModelScope)

    fun initUserCollectionsIds(currentUserCollectionsIds: MutableList<String>?) {
        _currentUserCollectionIds.postValue(currentUserCollectionsIds)
    }

    // Create new collection and add a photo into it
    fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?,
        photoId: String
    ): LiveData<BackendResult<Collection>> = liveData(viewModelScope.coroutineContext) {
        emit(BackendResult.Loading)

        val creationResult = collectionsRepository.createCollection(title, description, isPrivate)
        if (creationResult is BackendResult.Success) {
            var newCollection = creationResult.value

            val additionResult =
                collectionsRepository.addPhotoToCollection(newCollection.id, photoId)
            if (additionResult is BackendResult.Success) {
                val newIdList = _currentUserCollectionIds.value ?: mutableListOf()
                newIdList.add(newCollection.id)
                _currentUserCollectionIds.postValue(newIdList)

                additionResult.value.collection?.let {
                    newCollection = it
                }
            }
        }
        emit(creationResult)
    }

    fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): LiveData<BackendResult<CollectionPhotoResult>> = liveData(viewModelScope.coroutineContext) {
        emit(BackendResult.Loading)

        val additionResult = collectionsRepository.addPhotoToCollection(collectionId, photoId)
        if (additionResult is BackendResult.Success) {
            val newIdList = _currentUserCollectionIds.value ?: mutableListOf()
            newIdList.add(collectionId)
            _currentUserCollectionIds.postValue(newIdList)
        }
        emit(additionResult)
    }

    fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): LiveData<BackendResult<CollectionPhotoResult>> = liveData(viewModelScope.coroutineContext) {
        emit(BackendResult.Loading)

        val deletionResult = collectionsRepository.deletePhotoFromCollection(collectionId, photoId)
        if (deletionResult is BackendResult.Success) {
            val newList = _currentUserCollectionIds.value ?: mutableListOf()
            newList.remove(collectionId)
            _currentUserCollectionIds.postValue(newList)
        }
        emit(deletionResult)
    }
}