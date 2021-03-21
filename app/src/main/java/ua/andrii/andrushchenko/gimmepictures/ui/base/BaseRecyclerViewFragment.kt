package ua.andrii.andrushchenko.gimmepictures.ui.base

import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerViewFragment<Entity : Any, VB : ViewBinding>(inflate: Inflate<VB>) :
    BaseFragment<VB>(inflate) {

    protected abstract val pagedAdapter: BasePagedAdapter<Entity>
}