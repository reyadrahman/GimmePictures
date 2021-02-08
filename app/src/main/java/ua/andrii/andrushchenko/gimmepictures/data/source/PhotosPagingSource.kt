package ua.andrii.andrushchenko.gimmepictures.data.source

import androidx.annotation.StringRes
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.api.PhotoService
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class PhotosPagingSource(
    private val photoService: PhotoService,
    private val order: Order
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            val photos = photoService.getPhotos(pageKey, 20, order.value)

            LoadResult.Page(
                data = photos,
                prevKey = if (pageKey == STARTING_PAGE_INDEX) null else pageKey - 1,
                nextKey = if (photos.isEmpty()) null else pageKey + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int =
        state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey
        } ?: STARTING_PAGE_INDEX

    companion object {
        enum class Order(@StringRes val titleRes: Int, val value: String) {
            LATEST(R.string.order_latest, "latest"),
            OLDEST(R.string.order_oldest, "oldest"),
            POPULAR(R.string.order_popular, "popular")
        }
    }
}