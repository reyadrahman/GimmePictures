package ua.andrii.andrushchenko.gimmepictures.ui.user

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.ListingLayoutBinding
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.photo.PhotosAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.widgets.AspectRatioImageView
import ua.andrii.andrushchenko.gimmepictures.util.setupStaggeredGridLayoutManager

@AndroidEntryPoint
class UserLikedPhotosFragment : BaseRecyclerViewFragment<Photo, ListingLayoutBinding>(
    ListingLayoutBinding::inflate
) {

    private val viewModel: UserDetailsViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override val pagedAdapter: BasePagedAdapter<Photo> =
        PhotosAdapter(object : PhotosAdapter.OnItemClickListener {
            override fun onPhotoClick(photo: Photo, photoImageView: AspectRatioImageView) {
                val direction =
                    UserDetailsFragmentDirections.actionGlobalPhotoDetailsFragment(photoId = photo.id)
                requireParentFragment().findNavController().navigate(direction)
            }
        })

    override val rv: RecyclerView
        get() = binding.recyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            textViewEmpty.text = getString(R.string.user_no_liked_photos)
            swipeRefreshLayout.setOnRefreshListener {
                pagedAdapter.refresh()
            }

            pagedAdapter.addLoadStateListener { loadState ->
                swipeRefreshLayout.isRefreshing =
                    loadState.refresh is LoadState.Loading
                rv.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                textViewError.isVisible =
                    loadState.source.refresh is LoadState.Error

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
            layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            setupStaggeredGridLayoutManager(resources.getDimensionPixelSize(R.dimen.indent_8dp))

            adapter = pagedAdapter.withLoadStateHeaderAndFooter(
                header = RecyclerViewLoadStateAdapter { pagedAdapter.retry() },
                footer = RecyclerViewLoadStateAdapter { pagedAdapter.retry() }
            )
        }

        viewModel.userLikedPhotos.observe(viewLifecycleOwner) {
            pagedAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }
}