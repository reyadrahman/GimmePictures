package ua.andrii.andrushchenko.gimmepictures.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.source.PhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotosBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.ui.adapters.PhotosAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.adapters.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.viewmodels.PhotoViewModel

@AndroidEntryPoint
class PhotosFragment : Fragment(R.layout.fragment_photos) {

    private val viewModel by viewModels<PhotoViewModel>()

    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPhotosBinding.bind(view)

        val adapter = PhotosAdapter(object : PhotosAdapter.OnItemClickListener {
            override fun onPhotoClick(photo: Photo) {
                val direction =
                    PhotosFragmentDirections.actionPhotosFragmentToPhotoDetailsFragment(photo.id)
                findNavController().navigate(direction)
            }

            override fun onUserClick(user: User) {
                // TODO navigate to user details
                Toast.makeText(requireContext(), "User ${user.name} selected", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = RecyclerViewLoadStateAdapter { adapter.retry() },
                footer = RecyclerViewLoadStateAdapter { adapter.retry() }
            )

            swipeRefreshLayout.setOnRefreshListener {
                adapter.refresh()
            }
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
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
        }

        viewModel.order.observe(viewLifecycleOwner) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                "${getString(it.titleRes)} ${getString(R.string.photos)}"
        }

        // Populate recyclerView
        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_photos, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sort) {
            showFilterDialog()
            return true
        } else if (item.itemId == R.id.action_search) {
            val direction = PhotosFragmentDirections.actionPhotosFragmentToSearchFragment("")
            findNavController().navigate(direction)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}