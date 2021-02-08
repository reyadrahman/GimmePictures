package ua.andrii.andrushchenko.gimmepictures.data.models

import ua.andrii.andrushchenko.gimmepictures.models.Collection

data class SearchCollectionsResult(
    val total: Int,
    val total_pages: Int,
    val results: List<Collection>
)