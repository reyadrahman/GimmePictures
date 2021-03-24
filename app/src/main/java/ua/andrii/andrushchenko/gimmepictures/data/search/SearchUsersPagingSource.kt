package ua.andrii.andrushchenko.gimmepictures.data.search

import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.data.base.BasePagingSource
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.common.STARTING_PAGE_INDEX
import ua.andrii.andrushchenko.gimmepictures.models.User
import java.io.IOException

class SearchUsersPagingSource(
    private val searchService: SearchService,
    private val query: String
) : BasePagingSource<User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = searchService.searchUsers(query, pageKey, PAGE_SIZE)
            val users = response.results

            LoadResult.Page(
                data = users,
                prevKey = if (pageKey == STARTING_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (users.isEmpty()) null else pageKey + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}