package ua.andrii.andrushchenko.gimmepictures.data.photos

import androidx.annotation.StringRes
import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.base.BasePagingSource
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.common.STARTING_PAGE_INDEX
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import java.io.IOException

class PhotosPagingSource(
    private val photoService: PhotoService,
    private val order: Order
) : BasePagingSource<Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            val photos: List<Photo> = photoService.getPhotos(pageKey, PAGE_SIZE, order.value)

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

    companion object {
        enum class Order(@StringRes val titleRes: Int, val value: String) {
            LATEST(R.string.order_latest, "latest"),
            OLDEST(R.string.order_oldest, "oldest"),
            POPULAR(R.string.order_popular, "popular")
        }
    }
}