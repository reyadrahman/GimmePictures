package ua.andrii.andrushchenko.gimmepictures.ui.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerViewFragment<Entity : Any, VB : ViewBinding>(inflate: Inflate<VB>) :
    BaseFragment<VB>(inflate) {

    protected abstract val pagedAdapter: BasePagedAdapter<Entity>
    protected abstract val rv: RecyclerView

    fun scrollRecyclerViewToTop() {
        rv.scrollToPosition(0)
    }
}