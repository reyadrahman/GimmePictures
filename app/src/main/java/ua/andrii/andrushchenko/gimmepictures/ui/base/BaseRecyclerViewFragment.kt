package ua.andrii.andrushchenko.gimmepictures.ui.base

import androidx.fragment.app.Fragment

abstract class BaseRecyclerViewFragment<Entity : Any> : Fragment() {

    protected abstract val pagedAdapter: BasePagedAdapter<Entity>

}