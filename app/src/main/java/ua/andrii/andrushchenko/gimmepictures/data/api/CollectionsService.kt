package ua.andrii.andrushchenko.gimmepictures.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ua.andrii.andrushchenko.gimmepictures.data.models.SearchCollectionsResult

interface CollectionsService {

    @GET("search/collections")
    suspend fun searchCollections(
        @Query("query") query: String,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): SearchCollectionsResult

}