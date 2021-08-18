package ua.andrii.andrushchenko.gimmepictures.data.user

import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.data.base.BasePagingSource
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.common.STARTING_PAGE_INDEX
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import java.io.IOException

class UserCollectionsPagingSource(
    private val userService: UserService,
    private val username: String
) : BasePagingSource<Collection>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            val userCollections: List<Collection> = userService.getUserCollections(username, pageKey, PAGE_SIZE)

            LoadResult.Page(
                data = userCollections,
                prevKey = if (pageKey == STARTING_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (userCollections.isEmpty()) null else pageKey + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}