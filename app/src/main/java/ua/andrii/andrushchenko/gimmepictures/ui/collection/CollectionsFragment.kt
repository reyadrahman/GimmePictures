package ua.andrii.andrushchenko.gimmepictures.ui.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.collection.CollectionsPagingSource
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentCollectionsBinding
import ua.andrii.andrushchenko.gimmepictures.models.Collection
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.util.setupLinearLayoutManager

@AndroidEntryPoint
class CollectionsFragment : BaseRecyclerViewFragment<Collection>() {

    private var _binding: FragmentCollectionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CollectionsViewModel by viewModels()

    override val pagedAdapter: BasePagedAdapter<Collection> =
        CollectionsAdapter(object : CollectionsAdapter.OnItemClickListener {
            override fun onCollectionClick(collection: Collection) {
                val direction =
                    CollectionsFragmentDirections.actionNavCollectionsToCollectionPhotosFragment()
                findNavController().navigate(direction)
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbar.apply {
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_sort -> {
                            showFilterDialog()
                        }
                        R.id.action_search -> {
                            val direction =
                                CollectionsFragmentDirections.actionNavCollectionsToSearchFragment(
                                    null)
                            findNavController().navigate(direction)
                        }
                    }
                    true
                }

                val navController = findNavController()
                val appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_photos,
                        R.id.nav_collections,
                        R.id.nav_my_profile
                    )
                )
                setupWithNavController(navController, appBarConfiguration)

                setOnClickListener {
                    recyclerView.scrollToPosition(0)
                }

                viewModel.order.observe(viewLifecycleOwner) {
                    title = "${getString(it.titleRes)} ${getString(R.string.collections)}"
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
                pagedAdapter.refresh()
            }

            pagedAdapter.addLoadStateListener { loadState ->
                swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    pagedAdapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                }
            }

            recyclerView.apply {
                setHasFixedSize(true)

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

            viewModel.collections.observe(viewLifecycleOwner) {
                pagedAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFilterDialog() {
        val orderOptions = enumValues<CollectionsPagingSource.Companion.Order>().map {
            getString(it.titleRes)
        }.toTypedArray()
        val currentSelection = viewModel.order.value?.ordinal ?: 0
        MaterialAlertDialogBuilder(requireContext()).run {
            setTitle(getString(R.string.sort_by))
            setSingleChoiceItems(orderOptions, currentSelection) { dialog, which ->
                if (which != currentSelection) viewModel.orderCollectionsBy(which)
                dialog.dismiss()
                binding.recyclerView.scrollToPosition(0)
            }
            create()
            show()
        }
    }
}