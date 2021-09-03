package ua.andrii.andrushchenko.gimmepictures.ui.collection

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentCollectionsBinding
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.util.setupLinearLayoutManager

@AndroidEntryPoint
class CollectionsFragment : BaseRecyclerViewFragment<Collection, FragmentCollectionsBinding>(
    FragmentCollectionsBinding::inflate
) {

    private val viewModel: CollectionsViewModel by viewModels()

    override val pagedAdapter: BasePagedAdapter<Collection> = CollectionsAdapter { collection ->
        val direction =
            CollectionsFragmentDirections.actionNavCollectionsToCollectionDetailsFragment(
                collection = collection
            )
        findNavController().navigate(direction)
    }

    override val rv: RecyclerView
        get() = binding.collectionsListingLayout.recyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            setupToolbar()

            collectionsListingLayout.swipeRefreshLayout.setOnRefreshListener {
                pagedAdapter.refresh()
            }

            pagedAdapter.addLoadStateListener { loadState ->
                collectionsListingLayout.swipeRefreshLayout.isRefreshing =
                    loadState.refresh is LoadState.Loading
                rv.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                collectionsListingLayout.textViewError.isVisible =
                    loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    pagedAdapter.itemCount < 1
                ) {
                    rv.isVisible = false
                }
            }
        }
        rv.apply {
            setHasFixedSize(true)
            setupLinearLayoutManager(resources.getDimensionPixelSize(R.dimen.indent_8dp))

            adapter = pagedAdapter.withLoadStateHeaderAndFooter(
                header = RecyclerViewLoadStateAdapter { pagedAdapter.retry() },
                footer = RecyclerViewLoadStateAdapter { pagedAdapter.retry() }
            )
        }

        // Populate recyclerView
        viewModel.collections.observe(viewLifecycleOwner) {
            pagedAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_photos,
                R.id.nav_collections,
                R.id.nav_account
            )
        )
        binding.toolbar.apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_search -> {
                        val direction =
                            CollectionsFragmentDirections.actionNavCollectionsToSearchFragment(
                                searchQuery = null
                            )
                        findNavController().navigate(direction)
                    }
                }
                true
            }

            setupWithNavController(navController, appBarConfiguration)

            setOnClickListener {
                scrollRecyclerViewToTop()
            }
        }
    }
}