package ua.andrii.andrushchenko.gimmepictures.ui.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotosBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.util.setupLayoutManager

@AndroidEntryPoint
class PhotosFragment : BaseRecyclerViewFragment<Photo>() {

    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!

    private val viewModel by hiltNavGraphViewModels<PhotoViewModel>(R.id.nav_main)

    override val pagedAdapter: BasePagedAdapter<Photo> =
        PhotosAdapter(object : PhotosAdapter.OnItemClickListener {
            override fun onPhotoClick(photo: Photo) {
                val direction =
                    PhotosFragmentDirections.actionGlobalPhotoDetailsFragment(photoId = photo.id)
                findNavController().navigate(direction)
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            toolbar.apply {
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_sort -> {
                            showFilterDialog()
                        }
                    }
                    true
                }

                val navController = findNavController()
                val appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_photos,
                        R.id.nav_collections,
                        R.id.nav_search,
                        R.id.nav_my_profile
                    )
                )
                setupWithNavController(navController, appBarConfiguration)

                setOnClickListener {
                    recyclerView.scrollToPosition(0)
                }

                viewModel.order.observe(viewLifecycleOwner) {
                    title = "${getString(it.titleRes)} ${getString(R.string.photos)}"
                }
            }

            recyclerView.setHasFixedSize(true)

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
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL).apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                }

                setupLayoutManager(
                    resources.configuration.orientation,
                    resources.getDimensionPixelSize(R.dimen.indent_8dp)
                )

                adapter = pagedAdapter.withLoadStateHeaderAndFooter(
                    header = RecyclerViewLoadStateAdapter { pagedAdapter.retry() },
                    footer = RecyclerViewLoadStateAdapter { pagedAdapter.retry() }
                )
            }
        }

        // Populate recyclerView
        viewModel.photos.observe(viewLifecycleOwner) {
            pagedAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFilterDialog() {
        val orderOptions =
            enumValues<PhotosPagingSource.Companion.Order>().map { getString(it.titleRes) }
                .toTypedArray()
        val currentSelection = viewModel.order.value?.ordinal ?: 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.sort_by))
            .setSingleChoiceItems(orderOptions, currentSelection) { dialog, which ->
                if (which != currentSelection) viewModel.orderPhotosBy(which)
                dialog.dismiss()
                binding.recyclerView.scrollToPosition(0)
            }
            .create()
            .show()
    }
}