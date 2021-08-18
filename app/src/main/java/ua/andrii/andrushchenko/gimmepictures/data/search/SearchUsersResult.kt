package ua.andrii.andrushchenko.gimmepictures.data.search

import com.google.gson.annotations.SerializedName
import ua.andrii.andrushchenko.gimmepictures.domain.User

data class SearchUsersResult(
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    val results: List<User>
)