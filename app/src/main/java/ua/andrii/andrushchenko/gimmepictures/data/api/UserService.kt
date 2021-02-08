package ua.andrii.andrushchenko.gimmepictures.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ua.andrii.andrushchenko.gimmepictures.data.models.SearchUsersResult

interface UserService {

    @GET("search/users")
    suspend fun searchUsers(
        @Query("query") query: String,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): SearchUsersResult

}