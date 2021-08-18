package ua.andrii.andrushchenko.gimmepictures.domain

import com.google.gson.annotations.SerializedName

data class Me(
    val id: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    val username: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("twitter_username")
    val twitterUsername: String?,
    @SerializedName("portfolio_url")
    val portfolioUrl: String?,
    val bio: String?,
    val location: String?,
    val links: Photo.Links?,
    @SerializedName("profile_image")
    val profileImage: User.ProfileImage?,
    @SerializedName("instagram_username")
    val instagramUsername: String?,
    @SerializedName("total_likes")
    val totalLikes: Int?,
    @SerializedName("total_photos")
    val totalPhotos: Int?,
    @SerializedName("total_collections")
    val totalCollections: Int?,
    val photos: List<Photo>?,
    @SerializedName("followed_by_user")
    val followedByUser: Boolean?,
    @SerializedName("followers_count")
    val followersCount: Int?,
    @SerializedName("following_count")
    val followingCount: Int?,
    val downloads: Int?,
    @SerializedName("uploads_remaining")
    val uploadsRemaining: Int?,
    val email: String?
)