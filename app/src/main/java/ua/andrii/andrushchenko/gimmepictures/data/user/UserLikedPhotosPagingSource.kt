package ua.andrii.andrushchenko.gimmepictures.data.user

import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.data.base.BasePagingSource
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.common.STARTING_PAGE_INDEX
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import java.io.IOException

class UserLikedPhotosPagingSource(
    private val userService: UserService,
    private val username: String
) : BasePagingSource<Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            val likedPhotos: List<Photo> = userService.getUserLikes(username, pageKey, PAGE_SIZE, "latest", null)

            LoadResult.Page(
                data = likedPhotos,
                prevKey = if (pageKey == STARTING_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (likedPhotos.isEmpty()) null else pageKey + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}