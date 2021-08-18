package ua.andrii.andrushchenko.gimmepictures.data.user

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import ua.andrii.andrushchenko.gimmepictures.domain.User
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult

interface UserRepository {

    suspend fun getUserPublicProfile(username: String): BackendResult<User>

    fun getUserLikedPhotos(username: String): LiveData<PagingData<Photo>>

    fun getUserPhotos(username: String): LiveData<PagingData<Photo>>

    fun getUserCollections(username: String): LiveData<PagingData<Collection>>

}