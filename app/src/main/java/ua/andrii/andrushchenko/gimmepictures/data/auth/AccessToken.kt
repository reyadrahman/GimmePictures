package ua.andrii.andrushchenko.gimmepictures.data.auth

data class AccessToken(
    val access_token: String,
    val token_type: String?,
    val scope: String?,
    val create_at: Int?
)