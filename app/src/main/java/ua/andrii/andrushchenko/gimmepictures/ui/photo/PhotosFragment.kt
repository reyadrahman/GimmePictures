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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.source.PhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotosBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.ui.activities.MainActivity
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter

@AndroidEntryPoint
class PhotosFragment : BaseRecyclerViewFragment<Photo>() {

    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!

    private val viewModel by hiltNavGraphViewModels<PhotoViewModel>(R.id.nav_main)

    override val adapter: BasePagedAdapter<Photo> =
        PhotosAdapter(object : PhotosAdapter.OnItemClickListener {
            override fun onPhotoClick(photo: Photo) {
                val direction =
                    PhotosFragmentDirections.actionNavPhotosToPhotoDetailsFragment(photoId = photo.id)
                findNavController().navigate(direction)
            }

            override fun onUserClick(user: User) {
                val direction =
                    PhotosFragmentDirections.actionNavPhotosToUsersFragment()
                findNavController().navigate(direction)
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_sort -> {
                        showFilterDialog()
                    }
                    R.id.action_search -> {
                        val direction =
                            PhotosFragmentDirections.actionNavPhotosToSearchFragment(searchQuery = "")
                        findNavController().navigate(direction)
                    }
                }
                true
            }

            val navController = findNavController()
            val appBarConfiguration =
                AppBarConfiguration(setOf(R.id.nav_photos, R.id.nav_collections))
            toolbar.setupWithNavController(navController, appBarConfiguration)

            recyclerView.setHasFixedSize(true)

            swipeRefreshLayout.setOnRefreshListener {
                adapter.refresh()
            }

            adapter.addLoadStateListener { loadState ->
                swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    //textViewEmpty.isVisible = true
                } /*else {
                    textViewEmpty.isVisible = false
                }*/
            }

            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = RecyclerViewLoadStateAdapter { adapter.retry() },
                footer = RecyclerViewLoadStateAdapter { adapter.retry() }
            )

            viewModel.order.observe(viewLifecycleOwner) {
                toolbar.title = "${getString(it.titleRes)} ${getString(R.string.photos)}"
            }

            toolbar.setOnClickListener {
                recyclerView.scrollToPosition(0)
            }
        }

        // Populate recyclerView
        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).toggleBottomNav(isVisible = true)
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