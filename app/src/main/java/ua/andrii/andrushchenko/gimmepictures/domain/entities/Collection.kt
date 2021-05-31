package ua.andrii.andrushchenko.gimmepictures.domain.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Collection(
    val id: String,
    val title: String,
    val description: String?,
    @SerializedName("published_at")
    val publishedAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val curated: Boolean?,
    val featured: Boolean?,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    val private: Boolean?,
    @SerializedName("share_key")
    val shareKey: String?,
    val tags: List<Photo.Tag>?,
    @SerializedName("cover_photo")
    val coverPhoto: Photo?,
    @SerializedName("preview_photos")
    val previewPhotos: List<Photo>?,
    val user: User?
) : Parcelable