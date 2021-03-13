package ua.andrii.andrushchenko.gimmepictures.data.collection

import androidx.annotation.StringRes
import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.base.BasePagingSource
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.common.STARTING_PAGE_INDEX
import ua.andrii.andrushchenko.gimmepictures.models.Collection
import java.io.IOException

class CollectionsPagingSource(
    private val collectionsService: CollectionsService,
    private val order: Order
) : BasePagingSource<Collection>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            val collections: List<Collection> = when (order) {
                Order.ALL -> collectionsService.getAllCollections(pageKey, PAGE_SIZE)
                Order.FEATURED -> collectionsService.getFeaturedCollections(pageKey, PAGE_SIZE)
            }

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

    companion object {
        enum class Order(@StringRes val titleRes: Int, val value: String) {
            ALL(R.string.order_all, "all"),
            FEATURED(R.string.order_featured, "featured")
        }
    }
}