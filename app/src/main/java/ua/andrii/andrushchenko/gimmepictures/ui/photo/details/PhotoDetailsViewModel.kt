package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.andrii.andrushchenko.gimmepictures.data.auth.AuthRepository
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionPhotoResult
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsRepository
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotosRepository
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Collection
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import java.util.*
import javax.inject.Inject

// This viewModel is shared between PhotoDetailsFragment and AddToCollection dialog fragment
// to implement 'add photo to collection' logic

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val photoRepository: PhotosRepository,
    private val collectionsRepository: CollectionsRepository
) : ViewModel() {

    val isUserAuthorized get() = authRepository.isAuthorized

    private val _authorizedUserNickName: MutableLiveData<String> = MutableLiveData(authRepository.userNickname)

    private val _photo: MutableLiveData<Photo> = MutableLiveData()
    val photo get() = _photo

    private val _error: MutableLiveData<Boolean> = MutableLiveData(false)
    val error get() = _error

    private val _currentUserCollectionIds = MutableLiveData<MutableList<String>?>()
    val currentUserCollectionIds: LiveData<MutableList<String>?> = _currentUserCollectionIds

    fun getPhotoDetails(photoId: String) = viewModelScope.launch {
        when (val result = photoRepository.getSinglePhoto(photoId)) {
            is BackendResult.Success -> {
                _photo.postValue(result.value)
                _currentUserCollectionIds.postValue(
                    result.value.currentUserCollections?.map { it.id }?.toMutableList()
                )
                _error.postValue(false)
            }
            is BackendResult.Error -> {
                _error.postValue(true)
            }
            else -> {
                _error.postValue(false)
            }
        }
    }

    fun likePhoto(id: String) = viewModelScope.launch { photoRepository.likePhoto(id) }

    fun dislikePhoto(id: String) = viewModelScope.launch { photoRepository.dislikePhoto(id) }

    fun notifyUserAuthorizationSuccessful() {
        _authorizedUserNickName.postValue(authRepository.userNickname)
    }

    val userCollections: LiveData<PagingData<Collection>> =
        _authorizedUserNickName.switchMap { userNickname ->
            collectionsRepository.getUserCollections(userNickname).cachedIn(viewModelScope)
        }

    // Create new collection and add a photo into it
    fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?,
        photoId: String
    ): LiveData<BackendResult<Collection>> = liveData {
        emit(BackendResult.Loading)

        val creationResult = collectionsRepository.createCollection(title, description, isPrivate)
        if (creationResult is BackendResult.Success) {
            var newCollection = creationResult.value

            val additionResult = collectionsRepository.addPhotoToCollection(newCollection.id, photoId)
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
    ): LiveData<BackendResult<CollectionPhotoResult>> =
        liveData(viewModelScope.coroutineContext) {
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
    ): LiveData<BackendResult<CollectionPhotoResult>> =
        liveData(viewModelScope.coroutineContext) {
            emit(BackendResult.Loading)

            val deletionResult = collectionsRepository.deletePhotoFromCollection(collectionId, photoId)
            if (deletionResult is BackendResult.Success) {
                val newList = _currentUserCollectionIds.value ?: mutableListOf()
                newList.remove(collectionId)
                _currentUserCollectionIds.postValue(newList)
            }
            emit(deletionResult)
        }

    var downloadWorkUUID: UUID? = null
}