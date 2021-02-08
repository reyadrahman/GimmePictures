package ua.andrii.andrushchenko.gimmepictures.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val username: String?,
    val name: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("instagram_username")
    val instagramUsername: String?,
    @SerializedName("twitter_username")
    val twitterUsername: String?,
    @SerializedName("portfolio_url")
    val portfolioUrl: String?,
    val bio: String?,
    val location: String?,
    @SerializedName("total_likes")
    val totalLikes: Int?,
    @SerializedName("total_photos")
    val totalPhotos: Int?,
    @SerializedName("total_collections")
    val totalCollections: Int?,
    @SerializedName("followed_by_user")
    val followedByUser: Boolean?,
    @SerializedName("followers_count")
    val followersCount: Int?,
    @SerializedName("following_count")
    val followingCount: Int?,
    val downloads: Int?,
    @SerializedName("profile_image")
    val profileImage: ProfileImage?,
    val badge: Badge?,
    val photos: List<Photo>?
) : Parcelable {

    @Parcelize
    data class ProfileImage(
        val small: String,
        val medium: String,
        val large: String
    ) : Parcelable

    @Parcelize
    data class Badge(
        val title: String?,
        val primary: Boolean?,
        val slug: String?,
        val link: String?
    ) : Parcelable
}