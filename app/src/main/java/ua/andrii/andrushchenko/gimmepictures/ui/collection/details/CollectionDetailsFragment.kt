package ua.andrii.andrushchenko.gimmepictures.ui.collection.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentCollectionDetailsBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.photo.PhotosAdapter
import ua.andrii.andrushchenko.gimmepictures.util.setupStaggeredGridLayoutManager
import ua.andrii.andrushchenko.gimmepictures.util.toAmountReadableString

@AndroidEntryPoint
class CollectionDetailsFragment : BaseRecyclerViewFragment<Photo, FragmentCollectionDetailsBinding>(
    FragmentCollectionDetailsBinding::inflate) {

    private val args: CollectionDetailsFragmentArgs by navArgs()
    private val viewModel: CollectionDetailsViewModel by viewModels()

    override val pagedAdapter: BasePagedAdapter<Photo> =
        PhotosAdapter(object : PhotosAdapter.OnItemClickListener {
            override fun onPhotoClick(photo: Photo) {
                val direction =
                    CollectionDetailsFragmentDirections.actionGlobalPhotoDetailsFragment(photoId = photo.id)
                findNavController().navigate(direction)
            }
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbar.apply {
                val navController = findNavController()
                val appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_photos,
                        R.id.nav_collections,
                        R.id.nav_my_profile
                    )
                )
                setupWithNavController(navController, appBarConfiguration)

                title = args.collection.title
            }

            collectionPhotosListingLayout.swipeRefreshLayout.setOnRefreshListener {
                pagedAdapter.refresh()
            }

            pagedAdapter.addLoadStateListener { loadState ->
                collectionPhotosListingLayout.swipeRefreshLayout.isRefreshing =
                    loadState.refresh is LoadState.Loading
                collectionPhotosListingLayout.recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                collectionPhotosListingLayout.textViewError.isVisible =
                    loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    pagedAdapter.itemCount < 1
                ) {
                    collectionPhotosListingLayout.recyclerView.isVisible = false
                }
            }

            collectionPhotosListingLayout.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                setupStaggeredGridLayoutManager(
                    resources.configuration.orientation,
                    resources.getDimensionPixelSize(R.dimen.indent_8dp)
                )

                adapter = pagedAdapter.withLoadStateHeaderAndFooter(
                    header = RecyclerViewLoadStateAdapter { pagedAdapter.retry() },
                    footer = RecyclerViewLoadStateAdapter { pagedAdapter.retry() }
                )
            }

            with(args.collection) {
                description?.let { description ->
                    descriptionTextView.apply {
                        visibility = View.VISIBLE
                        text = description
                    }
                }

                @SuppressLint("SetTextI18n")
                userNameTextView.text = "${
                    totalPhotos.toAmountReadableString()
                } ${
                    getString(R.string.photos)
                } ${
                    getString(R.string.curated_by)
                } ${
                    user?.username
                }"

                // Request data only if the fragment has been created at the first time
                if (savedInstanceState == null) {
                    viewModel.getCollectionPhotos(id)
                }

                viewModel.collectionPhotos.observe(viewLifecycleOwner) {
                    pagedAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
        }
    }
}