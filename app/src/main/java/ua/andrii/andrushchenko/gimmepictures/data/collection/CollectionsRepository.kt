package ua.andrii.andrushchenko.gimmepictures.data.collection

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult

interface CollectionsRepository {

    fun getCollections(): LiveData<PagingData<Collection>>

    fun getCollectionPhotos(collectionId: String): LiveData<PagingData<Photo>>

    fun getUserCollections(userNickname: String): LiveData<PagingData<Collection>>

    suspend fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?
    ): BackendResult<Collection>

    suspend fun updateCollection(
        id: String,
        title: String?,
        description: String?,
        isPrivate: Boolean
    ): BackendResult<Collection>

    suspend fun deleteCollection(id: String): BackendResult<Unit>

    suspend fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): BackendResult<CollectionPhotoResult>

    suspend fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): BackendResult<CollectionPhotoResult>

}