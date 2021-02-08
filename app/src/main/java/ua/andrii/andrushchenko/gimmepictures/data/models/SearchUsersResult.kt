package ua.andrii.andrushchenko.gimmepictures.data.models

import ua.andrii.andrushchenko.gimmepictures.models.User

data class SearchUsersResult(
    val total: Int,
    val total_pages: Int,
    val results: List<User>
)