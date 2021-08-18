package ua.andrii.andrushchenko.gimmepictures.ui.collection.details

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
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentCollectionDetailsBinding
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.photo.PhotosAdapter
import ua.andrii.andrushchenko.gimmepictures.util.setupStaggeredGridLayoutManager
import ua.andrii.andrushchenko.gimmepictures.util.toReadableString

@AndroidEntryPoint
class CollectionDetailsFragment : BaseRecyclerViewFragment<Photo, FragmentCollectionDetailsBinding>(
    FragmentCollectionDetailsBinding::inflate
) {

    private val args: CollectionDetailsFragmentArgs by navArgs()
    private val viewModel: CollectionDetailsViewModel by viewModels()

    override val pagedAdapter: BasePagedAdapter<Photo> = PhotosAdapter { photo ->
        val direction = CollectionDetailsFragmentDirections.actionGlobalPhotoDetailsFragment(
            photoId = photo.id
        )
        findNavController().navigate(direction)
    }

    override val rv: RecyclerView
        get() = binding.collectionPhotosListingLayout.recyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            setupToolbar()

            if (savedInstanceState == null) {
                viewModel.setCollection(args.collection)
            }

            viewModel.collection.observe(viewLifecycleOwner) { collection ->
                toolbar.title = collection.title

                collection.description?.let {
                    descriptionTextView.apply {
                        visibility = View.VISIBLE
                        text = it
                    }
                }

                userNameTextView.apply {
                    setOnClickListener {
                        collection.user?.username?.let {
                            val direction =
                                CollectionDetailsFragmentDirections
                                    .actionCollectionDetailsFragmentToUserDetailsFragment(
                                        user = null,
                                        username = it
                                    )
                            findNavController().navigate(direction)
                        }
                    }


                    text = getString(
                        R.string.collection_photos_amount_curated_by_formatted,
                        collection.totalPhotos.toReadableString(),
                        getString(R.string.photos).apply { first().lowercaseChar() },
                        getString(R.string.curated_by),
                        collection.user?.username
                    )
                }

                if (viewModel.isUserAuthorized && viewModel.isOwnCollection) {
                    fabEditCollection.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            val direction = CollectionDetailsFragmentDirections
                                .actionCollectionDetailsFragmentToEditCollectionDialogFragment(
                                    collection
                                )
                            findNavController().navigate(direction)
                        }
                    }
                }
            }

            viewModel.isDeleted.observe(viewLifecycleOwner) { isDeleted ->
                if (isDeleted) findNavController().navigateUp()
            }

            collectionPhotosListingLayout.swipeRefreshLayout.setOnRefreshListener {
                pagedAdapter.refresh()
            }

            pagedAdapter.addLoadStateListener { loadState ->
                collectionPhotosListingLayout.swipeRefreshLayout.isRefreshing =
                    loadState.refresh is LoadState.Loading
                rv.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                collectionPhotosListingLayout.textViewError.isVisible =
                    loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    pagedAdapter.itemCount < 1
                ) {
                    rv.isVisible = false
                }
            }

            rv.apply {
                setHasFixedSize(true)
                setupStaggeredGridLayoutManager(resources.getDimensionPixelSize(R.dimen.indent_8dp))

                adapter = pagedAdapter.withLoadStateHeaderAndFooter(
                    header = RecyclerViewLoadStateAdapter { pagedAdapter.retry() },
                    footer = RecyclerViewLoadStateAdapter { pagedAdapter.retry() }
                )
            }
        }

        viewModel.collectionPhotos.observe(viewLifecycleOwner) {
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
            setupWithNavController(navController, appBarConfiguration)
            setOnClickListener { scrollRecyclerViewToTop() }
        }
    }
}