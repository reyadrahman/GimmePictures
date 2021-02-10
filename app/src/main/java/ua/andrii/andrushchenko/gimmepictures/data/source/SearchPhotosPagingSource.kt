package ua.andrii.andrushchenko.gimmepictures.data.source

import androidx.annotation.StringRes
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.api.SearchService
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class SearchPhotosPagingSource(
    private val searchService: SearchService,
    private val query: String,
    private val order: Order,
    private val collections: String?,
    private val contentFilter: ContentFilter,
    private val color: Color,
    private val orientation: Orientation
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = searchService.searchPhotos(
                query = query,
                page = pageKey,
                per_page = PAGE_SIZE,
                order_by = order.value,
                collections = collections,
                contentFilter = contentFilter.value,
                color = color.value,
                orientation = orientation.value
            )

            val photos = response.results

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
        enum class Order(val value: String) {
            LATEST("latest"),
            RELEVANT("relevant")
        }

        enum class ContentFilter(val value: String) {
            LOW("low"),
            HIGH("high")
        }

        enum class Color(@StringRes val titleRes: Int, val value: String?) {
            ANY(R.string.filter_color_any, null),
            BLACK_AND_WHITE(R.string.filter_color_black_white, "black_and_white"),
            BLACK(R.string.filter_color_black, "black"),
            WHITE(R.string.filter_color_white, "white"),
            YELLOW(R.string.filter_color_yellow, "yellow"),
            ORANGE(R.string.filter_color_orange, "orange"),
            RED(R.string.filter_color_red, "red"),
            PURPLE(R.string.filter_color_purple, "purple"),
            MAGENTA(R.string.filter_color_magenta, "magenta"),
            GREEN(R.string.filter_color_green, "green"),
            TEAL(R.string.filter_color_teal, "teal"),
            BLUE(R.string.filter_color_blue, "blue")
        }

        enum class Orientation(val value: String?) {
            ANY(null),
            LANDSCAPE("landscape"),
            PORTRAIT("portrait"),
            SQUARISH("squarish")
        }
    }

}