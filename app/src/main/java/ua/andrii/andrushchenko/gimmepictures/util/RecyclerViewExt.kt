package ua.andrii.andrushchenko.gimmepictures.util

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.graphics.Rect
import android.view.View

fun RecyclerView.setupLayoutManager(
    orientation: Int,
    spacing: Int,
) {
    val decorationIndex = 0
    val spanCount =
        if (orientation == ORIENTATION_LANDSCAPE) 3 else 2

    (layoutManager as? StaggeredGridLayoutManager)?.spanCount = spanCount

    if (itemDecorationCount != 0) {
        removeItemDecorationAt(decorationIndex)
    }

    addItemDecoration(
        StaggeredGridItemOffsetDecoration(
            spacing,
            spanCount
        ),
        decorationIndex
    )
}

class StaggeredGridItemOffsetDecoration(
    private val spacing: Int,
    private val spanCount: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val params = view.layoutParams as? StaggeredGridLayoutManager.LayoutParams
        val spanIndex = params?.spanIndex ?: 0
        val position = params?.bindingAdapterPosition ?: 0

        outRect.left = if (spanIndex == 0) spacing else 0
        outRect.top = if (position < spanCount) spacing else 0
        outRect.right = spacing
        outRect.bottom = spacing
    }
}