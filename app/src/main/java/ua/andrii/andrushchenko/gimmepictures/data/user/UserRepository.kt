package ua.andrii.andrushchenko.gimmepictures.data.user

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.models.Collection
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.backendRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userService: UserService) {

    suspend fun getUserPublicProfile(username: String): BackendResult<User> = backendRequest {
        userService.getUserPublicProfile(username)
    }

    fun getUserLikedPhotos(username: String): LiveData<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserLikedPhotosPagingSource(userService, username) }
        ).liveData

    fun getUserPhotos(username: String): LiveData<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserPhotosPagingSource(userService, username) }
        ).liveData

    fun getUserCollections(username: String): LiveData<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserCollectionsPagingSource(userService, username) }
        ).liveData
}