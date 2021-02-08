package ua.andrii.andrushchenko.gimmepictures.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ua.andrii.andrushchenko.gimmepictures.data.models.SearchPhotosResult
import ua.andrii.andrushchenko.gimmepictures.models.Photo

interface PhotoService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?,
        @Query("order_by") order_by: String?
    ): List<Photo>

    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String
    ): Photo

    @GET("photos/random")
    suspend fun getRandomPhotos(
        @Query("collections") collectionsId: Int?,
        @Query("featured") featured: Boolean?,
        @Query("username") username: String?,
        @Query("query") query: String?,
        @Query("orientation") orientation: String?,
        @Query("content_filter") contentFilter: String?,
        @Query("count") count: Int?
    ): List<Photo>

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

}