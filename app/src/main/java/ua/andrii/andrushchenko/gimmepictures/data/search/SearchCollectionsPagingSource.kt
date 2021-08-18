package ua.andrii.andrushchenko.gimmepictures.data.search

import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.data.base.BasePagingSource
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.common.STARTING_PAGE_INDEX
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import java.io.IOException

class SearchCollectionsPagingSource(
    private val searchService: SearchService,
    private val query: String
) : BasePagingSource<Collection>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response: SearchCollectionsResult = searchService.searchCollections(query, pageKey, PAGE_SIZE)
            val collections: List<Collection> = response.results

            LoadResult.Page(
                data = collections,
                prevKey = if (pageKey == STARTING_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (collections.isEmpty()) null else pageKey + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}