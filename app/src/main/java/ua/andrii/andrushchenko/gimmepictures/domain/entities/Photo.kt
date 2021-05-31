package ua.andrii.andrushchenko.gimmepictures.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val id: String,
    val width: Int,
    val height: Int,
    @SerializedName("blur_hash")
    val blurHash: String?,
    val color: String? = "#E0E0E0",
    val views: Int?,
    val downloads: Int?,
    val likes: Int?,
    @SerializedName("liked_by_user")
    var likedByUser: Boolean,
    val description: String?,
    @SerializedName("alt_description")
    val altDescription: String?,
    val exif: Exif?,
    val location: Location?,
    val tags: List<Tag>?,
    @SerializedName("current_user_collections")
    val currentUserCollections: List<Collection>?,
    val sponsorship: Sponsorship?,
    val urls: Urls,
    val links: Links?,
    val user: User?,
    val statistics: PhotoStatistics?
) : Parcelable {

    @Parcelize
    data class Exif(
        val make: String?,
        val model: String?,
        @SerializedName("exposure_time")
        val exposureTime: String?,
        val aperture: String?,
        @SerializedName("focal_length")
        val focalLength: String?,
        val iso: Int?
    ) : Parcelable

    @Parcelize
    data class Urls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String
    ) : Parcelable

    @Parcelize
    data class Links(
        val self: String,
        val html: String,
        val download: String,
        @SerializedName("download_location")
        val downloadLocation: String
    ) : Parcelable

    @Parcelize
    data class Location(
        val city: String?,
        val country: String?,
        val position: Position?
    ) : Parcelable

    @Parcelize
    data class Position(
        val latitude: Double?,
        val longitude: Double?
    ) : Parcelable

    @Parcelize
    data class Tag(val title: String?) : Parcelable

    @Parcelize
    data class Sponsorship(val sponsor: User?) : Parcelable
}