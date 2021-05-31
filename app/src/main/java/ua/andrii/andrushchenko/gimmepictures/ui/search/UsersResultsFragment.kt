package ua.andrii.andrushchenko.gimmepictures.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.ListingLayoutBinding
import ua.andrii.andrushchenko.gimmepictures.domain.entities.User
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.user.UsersAdapter
import ua.andrii.andrushchenko.gimmepictures.util.setupLinearLayoutManager

class UsersResultsFragment :
    BaseRecyclerViewFragment<User, ListingLayoutBinding>(ListingLayoutBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override val pagedAdapter: BasePagedAdapter<User> =
        UsersAdapter(object : UsersAdapter.OnItemClickListener {
            override fun onUserClick(user: User) {
                val direction = SearchFragmentDirections.actionSearchFragmentToUserDetailsFragment(user = user, username = null)
                requireParentFragment().findNavController().navigate(direction)
            }
        })

    override val rv: RecyclerView
        get() = binding.recyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            textViewEmpty.text = getString(R.string.nothing_found)
            swipeRefreshLayout.setOnRefreshListener {
                pagedAdapter.refresh()
            }

            pagedAdapter.addLoadStateListener { loadState ->
                swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading
                rv.isVisible = loadState.source.refresh is LoadState.NotLoading
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading) {
                    if (/*loadState.append.endOfPaginationReached && */pagedAdapter.itemCount < 1) {
                        rv.isVisible = false
                        textViewEmpty.isVisible = true
                    } else {
                        textViewEmpty.isVisible = false
                    }
                }
            }
        }

        rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            setupLinearLayoutManager(
                resources.getDimensionPixelSize(R.dimen.indent_8dp),
                resources.getDimensionPixelSize(R.dimen.indent_48dp),
                RecyclerView.VERTICAL
            )

            adapter = pagedAdapter.withLoadStateHeaderAndFooter(
                header = RecyclerViewLoadStateAdapter { pagedAdapter.retry() },
                footer = RecyclerViewLoadStateAdapter { pagedAdapter.retry() }
            )
        }

        viewModel.usersResult.observe(viewLifecycleOwner) {
            pagedAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }
}