package ua.andrii.andrushchenko.gimmepictures.data.search

import ua.andrii.andrushchenko.gimmepictures.models.Photo

data class SearchPhotosResult(
    val total: Int,
    val total_pages: Int,
    val results: List<Photo>
)