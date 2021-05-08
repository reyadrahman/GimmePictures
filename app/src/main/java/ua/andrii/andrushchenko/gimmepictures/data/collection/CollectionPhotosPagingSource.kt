package ua.andrii.andrushchenko.gimmepictures.data.collection

import retrofit2.HttpException
import ua.andrii.andrushchenko.gimmepictures.data.base.BasePagingSource
import ua.andrii.andrushchenko.gimmepictures.data.common.PAGE_SIZE
import ua.andrii.andrushchenko.gimmepictures.data.common.STARTING_PAGE_INDEX
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import java.io.IOException

class CollectionPhotosPagingSource(
    private val collectionsService: CollectionsService,
    private val collectionId: String
) : BasePagingSource<Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {

            val photos = collectionsService.getCollectionPhotos(collectionId, pageKey, PAGE_SIZE)

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
}