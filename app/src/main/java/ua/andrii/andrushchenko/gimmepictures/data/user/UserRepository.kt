package ua.andrii.andrushchenko.gimmepictures.data.user

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Collection
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo
import ua.andrii.andrushchenko.gimmepictures.domain.entities.User
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult

interface UserRepository {

    suspend fun getUserPublicProfile(username: String): BackendResult<User>

    fun getUserLikedPhotos(username: String): LiveData<PagingData<Photo>>

    fun getUserPhotos(username: String): LiveData<PagingData<Photo>>

    fun getUserCollections(username: String): LiveData<PagingData<Collection>>

}