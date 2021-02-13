package ua.andrii.andrushchenko.gimmepictures.ui.fragments

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import ua.andrii.andrushchenko.gimmepictures.ui.adapters.BasePagedAdapter

abstract class BaseRecyclerViewFragment<Entity : Any>(@LayoutRes layoutInt: Int) : Fragment(layoutInt) {

    protected abstract var _binding: ViewBinding?
    abstract val binding: ViewBinding

    protected abstract val viewModel: ViewModel

    protected abstract val adapter: BasePagedAdapter<Entity>

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}