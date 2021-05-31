package ua.andrii.andrushchenko.gimmepictures.data.user

import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Collection
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Me
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo
import ua.andrii.andrushchenko.gimmepictures.domain.entities.User

interface UserService {

    @GET("users/{username}")
    suspend fun getUserPublicProfile(
        @Path("username") username: String
    ): User

    /*@GET("users/{username}/portfolio")
    suspend fun getUserPortfolioLink(
        @Path("username") username: String
    ): ResponseBody*/

    @GET("users/{username}/photos")
    suspend fun getUserPhotos(
        @Path("username") username: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("order_by") orderBy: String?,
        @Query("stats") stats: Boolean?,
        @Query("resolution") resolution: String?,
        @Query("quantity") quantity: Int?,
        @Query("orientation") orientation: String?
    ): List<Photo>

    @GET("users/{username}/likes")
    suspend fun getUserLikes(
        @Path("username") username: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?,
        @Query("order_by") orderBy: String?,
        @Query("orientation") orientation: String?
    ): List<Photo>

    @GET("users/{username}/collections")
    suspend fun getUserCollections(
        @Path("username") username: String,
        @Query("page") page: Int?,
        @Query("per_page") perPage: Int?
    ): List<Collection>

    /*@GET("users/{username}/statistics")
    suspend fun getUserStatistics(
        @Path("username") username: String,
        @Query("resolution") resolution: String?,
        @Query("quantity") quantity: Int?
    ): PhotoStatistics.UserStatistics*/

    @GET("me")
    suspend fun getUserPrivateProfile(): Me

    @PUT("me")
    suspend fun updateUserPrivateProfile(
        @Query("username") username: String?,
        @Query("first_name") firstName: String?,
        @Query("last_name") lastName: String?,
        @Query("email") email: String?,
        @Query("url") url: String?,
        @Query("instagram_username") instagramUsername: String?,
        @Query("location") location: String?,
        @Query("bio") bio: String?
    ): Me
}