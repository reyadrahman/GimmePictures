package ua.andrii.andrushchenko.gimmepictures.data.collection

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.user.UserCollectionsPagingSource
import ua.andrii.andrushchenko.gimmepictures.data.user.UserService
import ua.andrii.andrushchenko.gimmepictures.models.Collection
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.backendRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionsRepository @Inject constructor(
    private val collectionsService: CollectionsService,
    private val userService: UserService
) {
    fun getCollections(): LiveData<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionsPagingSource(collectionsService) }
        ).liveData

    fun getCollectionPhotos(collectionId: String): LiveData<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CollectionPhotosPagingSource(collectionsService, collectionId) }
        ).liveData

    fun getUserCollections(userNickname: String): LiveData<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserCollectionsPagingSource(userService, userNickname) }
        ).liveData

    suspend fun createCollection(
        title: String,
        description: String?,
        isPrivate: Boolean?,
    ): BackendResult<Collection> =
        backendRequest {
            collectionsService.createCollection(title, description, isPrivate)
        }

    suspend fun updateCollection(
        id: String,
        title: String?,
        description: String?,
        isPrivate: Boolean
    ): BackendResult<Collection> = backendRequest {
        collectionsService.updateCollection(id, title, description, isPrivate)
    }

    suspend fun deleteCollection(id: String): BackendResult<Unit> = backendRequest {
        collectionsService.deleteCollection(id)
    }

    suspend fun addPhotoToCollection(
        collectionId: String,
        photoId: String
    ): BackendResult<CollectionPhotoResult> = backendRequest {
        collectionsService.addPhotoToCollection(collectionId, photoId)
    }

    suspend fun deletePhotoFromCollection(
        collectionId: String,
        photoId: String
    ): BackendResult<CollectionPhotoResult> = backendRequest {
        collectionsService.deletePhotoFromCollection(collectionId, photoId)
    }
}