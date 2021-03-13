package ua.andrii.andrushchenko.gimmepictures.data.collection

import retrofit2.Response
import retrofit2.http.*
import ua.andrii.andrushchenko.gimmepictures.models.Collection

interface CollectionsService {

    @GET("collections")
    suspend fun getAllCollections(
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): List<Collection>

    @GET("collections/featured")
    suspend fun getFeaturedCollections(
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): List<Collection>

    @GET("collections/{id}")
    suspend fun getCollection(
        @Path("id") id: Int
    ): Collection

    @GET("collections/{id}/related")
    suspend fun getRelatedCollections(
        @Path("id") id: Int
    ): List<Collection>

    @POST("collections")
    suspend fun createCollection(
        @Query("title") title: String,
        @Query("description") description: String?,
        @Query("private") private: Boolean?
    ): Collection

    @PUT("collections/{id}")
    suspend fun updateCollection(
        @Path("id") id: Int,
        @Query("title") title: String?,
        @Query("description") description: String?,
        @Query("private") private: Boolean?
    ): Collection

    @DELETE("collections/{id}")
    suspend fun deleteCollection(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("collections/{collection_id}/add")
    suspend fun addPhotoToCollection(
        @Path("collection_id") collection_id: Int,
        @Query("photo_id") photo_id: String
    ): CollectionPhotoResult

    @DELETE("collections/{collection_id}/remove")
    suspend fun removePhotoFromCollection(
        @Path("collection_id") collection_id: Int,
        @Query("photo_id") photo_id: String
    ): CollectionPhotoResult
}