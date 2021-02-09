package ua.andrii.andrushchenko.gimmepictures.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ua.andrii.andrushchenko.gimmepictures.data.models.SearchCollectionsResult
import ua.andrii.andrushchenko.gimmepictures.data.models.SearchPhotosResult
import ua.andrii.andrushchenko.gimmepictures.data.models.SearchUsersResult

interface SearchService {

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?,
        @Query("order_by") order_by: String?,
        @Query("collections") collections: String?,
        @Query("content_filter") contentFilter: String?,
        @Query("color") color: String?,
        @Query("orientation") orientation: String?
    ): SearchPhotosResult

    @GET("search/collections")
    suspend fun searchCollections(
        @Query("query") query: String,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): SearchCollectionsResult

    @GET("search/users")
    suspend fun searchUsers(
        @Query("query") query: String,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): SearchUsersResult
}