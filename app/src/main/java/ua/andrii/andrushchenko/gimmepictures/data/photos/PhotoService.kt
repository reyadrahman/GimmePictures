package ua.andrii.andrushchenko.gimmepictures.data.photos

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
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

    @POST("photos/{id}/like")
    suspend fun likePhoto(
        @Path("id") id: String
    ): ResponseBody

    @DELETE("photos/{id}/like")
    suspend fun unlikePhoto(
        @Path("id") id: String
    ): Response<Unit>

    /*@PUT("photos/{id}")
    suspend fun updatePhoto(
        @Path("id") id: String,
        @Query("description") description: String?,
        @Query("show_on_profile") show_on_profile: Boolean?,
        @Query("tags") tags: List<Tag>?,
        @Query("location[latitude]") latitude: Double?,
        @Query("location[longitude]") longitude: Double?,
        @Query("location[name]") name: String?,
        @Query("location[city]") city: String?,
        @Query("location[country]") country: String?,
        @Query("exif[make]") make: String?,
        @Query("exif[model]") model: String?,
        @Query("exif[exposure_time]") exposure_time: String?,
        @Query("exif[aperture_value]") aperture_value: String?,
        @Query("exif[focal_length]") focal_length: String?,
        @Query("exif[iso_speed_ratings]") iso_speed_ratings: Int?
    ): Photo*/

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
}