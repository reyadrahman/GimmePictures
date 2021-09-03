package ua.andrii.andrushchenko.gimmepictures.ui.photo

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotosBinding
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.util.CustomTabsHelper
import ua.andrii.andrushchenko.gimmepictures.util.setupStaggeredGridLayoutManager
import ua.andrii.andrushchenko.gimmepictures.util.showAlertDialogWithRadioButtons
import java.util.*

@AndroidEntryPoint
class PhotosFragment : BaseRecyclerViewFragment<Photo, FragmentPhotosBinding>(
    FragmentPhotosBinding::inflate
) {

    private val viewModel: PhotoViewModel by viewModels()

    override val pagedAdapter: BasePagedAdapter<Photo> = PhotosAdapter { photo ->
        val direction = PhotosFragmentDirections.actionGlobalPhotoDetailsFragment(
            photoId = photo.id
        )
        findNavController().navigate(direction)
    }

    override val rv: RecyclerView
        get() = binding.photoListingLayout.recyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            setupToolbar()

            photoListingLayout.swipeRefreshLayout.setOnRefreshListener {
                pagedAdapter.refresh()
            }

            pagedAdapter.addLoadStateListener { loadState ->
                photoListingLayout.swipeRefreshLayout.isRefreshing =
                    loadState.refresh is LoadState.Loading
                rv.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                photoListingLayout.textViewError.isVisible =
                    loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    pagedAdapter.itemCount < 1
                ) {
                    rv.isVisible = false
                }
            }

            fabAddPhoto.setOnClickListener {
                openAddPhotoTab()
            }
        }

        rv.apply {
            setHasFixedSize(true)
            setupStaggeredGridLayoutManager(resources.getDimensionPixelSize(R.dimen.indent_8dp))

            adapter = pagedAdapter.withLoadStateHeaderAndFooter(
                header = RecyclerViewLoadStateAdapter { pagedAdapter.retry() },
                footer = RecyclerViewLoadStateAdapter { pagedAdapter.retry() }
            )

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        // Scroll Down
                        if (binding.fabAddPhoto.isShown) {
                            binding.fabAddPhoto.hide()
                        }
                    } else if (dy < 0) {
                        // Scroll Up
                        if (!binding.fabAddPhoto.isShown) {
                            binding.fabAddPhoto.show()
                        }
                    }
                }
            })
        }

        // Populate recyclerView
        viewModel.photos.observe(viewLifecycleOwner) {
            pagedAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun openAddPhotoTab() {
        val uri = if (viewModel.isAuthorized) {
            Uri.parse(getString(R.string.unsplash_add_photo_authorized_url))
        } else {
            Uri.parse(getString(R.string.unsplash_add_photo_unauthorized_url))
        }
        CustomTabsHelper.openCustomTab(requireContext(), uri)
    }

    private fun showFilterDialog() {
        val orderOptions = enumValues<PhotosPagingSource.Companion.Order>()
            .map { getString(it.titleRes) }
            .toTypedArray()

        val currentSelection = viewModel.order.value?.ordinal ?: 0

        requireContext().showAlertDialogWithRadioButtons(
            R.string.sort_by,
            orderOptions,
            currentSelection
        ) { dialog, which ->
            if (which != currentSelection) viewModel.orderPhotosBy(which)
            dialog.dismiss()
            scrollRecyclerViewToTop()
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
                    R.id.action_sort -> {
                        showFilterDialog()
                    }
                    R.id.action_search -> {
                        val direction =
                            PhotosFragmentDirections.actionNavPhotosToSearchFragment(null)
                        findNavController().navigate(direction)
                    }
                }
                true
            }

            setupWithNavController(navController, appBarConfiguration)

            setOnClickListener {
                scrollRecyclerViewToTop()
            }

            viewModel.order.observe(viewLifecycleOwner) { order ->
                title = "${getString(order.titleRes)} ${
                    getString(R.string.photos).replaceFirstChar { it.lowercase(Locale.ROOT) }
                }"
            }
        }
    }
}