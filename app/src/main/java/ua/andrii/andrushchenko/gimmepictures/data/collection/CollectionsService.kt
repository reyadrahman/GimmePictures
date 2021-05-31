package ua.andrii.andrushchenko.gimmepictures.data.collection

import retrofit2.Response
import retrofit2.http.*
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Collection
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo

interface CollectionsService {

    @GET("collections")
    suspend fun getCollections(
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?
    ): List<Collection>

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id") id: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?
    ): List<Photo>

    /*@GET("collections/{id}")
    suspend fun getCollection(
        @Path("id") id: String
    ): Collection

    @GET("collections/{id}/related")
    suspend fun getRelatedCollections(
        @Path("id") id: Int
    ): List<Collection>*/

    @POST("collections")
    suspend fun createCollection(
        @Query("title") title: String,
        @Query("description") description: String?,
        @Query("private") isPrivate: Boolean?
    ): Collection

    @PUT("collections/{id}")
    suspend fun updateCollection(
        @Path("id") id: String,
        @Query("title") title: String?,
        @Query("description") description: String?,
        @Query("private") isPrivate: Boolean?
    ): Collection

    @DELETE("collections/{id}")
    suspend fun deleteCollection(
        @Path("id") id: String
    ): Response<Unit>

    @POST("collections/{collection_id}/add")
    suspend fun addPhotoToCollection(
        @Path("collection_id") collectionId: String,
        @Query("photo_id") photoId: String
    ): CollectionPhotoResult

    @DELETE("collections/{collection_id}/remove")
    suspend fun deletePhotoFromCollection(
        @Path("collection_id") collectionId: String,
        @Query("photo_id") photoId: String
    ): CollectionPhotoResult
}