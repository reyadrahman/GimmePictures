package ua.andrii.andrushchenko.gimmepictures.data.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ua.andrii.andrushchenko.gimmepictures.data.common.STARTING_PAGE_INDEX

abstract class BasePagingSource<Entity : Any> : PagingSource<Int, Entity>() {

    override fun getRefreshKey(state: PagingState<Int, Entity>): Int =
        state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey
        } ?: STARTING_PAGE_INDEX

}