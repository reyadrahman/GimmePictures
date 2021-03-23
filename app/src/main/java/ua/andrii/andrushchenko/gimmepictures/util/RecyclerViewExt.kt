package ua.andrii.andrushchenko.gimmepictures.util

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

fun RecyclerView.setupStaggeredGridLayoutManager(
    orientation: Int,
    margin: Int,
) {
    val decorationIndex = 0
    val spanCount =
        if (orientation == ORIENTATION_LANDSCAPE) 3 else 2

    (layoutManager as? StaggeredGridLayoutManager)?.apply {
        this.spanCount = spanCount
        gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
    }

    if (itemDecorationCount != 0) {
        removeItemDecorationAt(decorationIndex)
    }

    addItemDecoration(
        StaggeredGridItemOffsetDecoration(
            margin,
            spanCount
        ),
        decorationIndex
    )
}

fun RecyclerView.setupLinearLayoutManager(
    @Px margin: Int,
    landscapeEdgeMargin: Int,
    orientation: Int
) {
    addItemDecoration(
        LinearLayoutSpacingDecoration(
            margin,
            landscapeEdgeMargin,
            orientation
        )
    )
}

class LinearLayoutSpacingDecoration(
    @Px private val margin: Int,
    @Px private val landscapeEdgeMargin: Int = margin,
    private val orientation: Int = RecyclerView.VERTICAL,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (orientation == RecyclerView.VERTICAL) {
            outRect.apply {
                top = if (position == 0) margin else 0
                bottom = margin
                right = margin
                left = margin
            }
        } else if (orientation == RecyclerView.HORIZONTAL) {
            outRect.apply {
                top = margin
                bottom = margin
                right = landscapeEdgeMargin
                left = if (position == 0) landscapeEdgeMargin else 0
            }
        }
    }
}

class StaggeredGridItemOffsetDecoration(
    private val margin: Int,
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

        outRect.apply {
            top = if (position < spanCount) margin else 0
            left = if (spanIndex == 0) margin else 0
            right = margin
            bottom = margin
        }
    }
}
