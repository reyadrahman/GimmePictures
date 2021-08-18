package ua.andrii.andrushchenko.gimmepictures.data.user

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import ua.andrii.andrushchenko.gimmepictures.domain.User
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.backendRequest

class UserRepositoryImpl(private val userService: UserService) : UserRepository{

    override suspend fun getUserPublicProfile(username: String): BackendResult<User> = backendRequest {
        userService.getUserPublicProfile(username)
    }

    override fun getUserLikedPhotos(username: String): LiveData<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserLikedPhotosPagingSource(userService, username) }
        ).liveData

    override fun getUserPhotos(username: String): LiveData<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserPhotosPagingSource(userService, username) }
        ).liveData

    override fun getUserCollections(username: String): LiveData<PagingData<Collection>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserCollectionsPagingSource(userService, username) }
        ).liveData
}