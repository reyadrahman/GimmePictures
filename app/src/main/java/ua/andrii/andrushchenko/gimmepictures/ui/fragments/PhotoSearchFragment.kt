package ua.andrii.andrushchenko.gimmepictures.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotoSearchBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.ui.adapters.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.adapters.PhotosAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.adapters.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.fragments.dialogs.SearchPhotoFilterDialog
import ua.andrii.andrushchenko.gimmepictures.ui.viewmodels.SearchViewModel

@AndroidEntryPoint
class PhotoSearchFragment : BaseRecyclerViewFragment<Photo>(R.layout.fragment_photo_search) {

    override var _binding: ViewBinding? = null
    override val binding: FragmentPhotoSearchBinding get() = _binding!! as FragmentPhotoSearchBinding

    private val args by navArgs<PhotoSearchFragmentArgs>()
    override val viewModel by hiltNavGraphViewModels<SearchViewModel>(R.id.nav_photos)

    override val adapter: BasePagedAdapter<Photo> =
        PhotosAdapter(object : PhotosAdapter.OnItemClickListener {
            override fun onPhotoClick(photo: Photo) {
                val direction =
                    PhotoSearchFragmentDirections.actionSearchFragmentToPhotoDetailsFragment(photo.id)
                findNavController().navigate(direction)
            }

            override fun onUserClick(user: User) {
                // TODO navigate to user details
                Toast.makeText(requireContext(), "User ${user.name} selected", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPhotoSearchBinding.bind(view)

        binding.apply {
            fabFilter.setOnClickListener {
                showPhotoFilterDialog()
            }

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
                    textNothingFound.isVisible = true
                } else {
                    textNothingFound.isVisible = false
                }
            }

            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = RecyclerViewLoadStateAdapter { adapter.retry() },
                footer = RecyclerViewLoadStateAdapter { adapter.retry() }
            )

            viewModel.photoResults.observe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchItem.expandActionView()

        val searchQuery = args.searchQuery
        searchView.setQuery(
            if (searchQuery.isNotBlank()) searchQuery else viewModel.query.value,
            searchQuery.isNotBlank()
        )

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.updateQuery(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                /*if (newText != null) {
                    if (newText.isEmpty()) {
                        viewModel.updateQuery(newText)
                    }
                }*/
                return true
            }
        })
    }

    // TODO: check it out
    private fun showPhotoFilterDialog() {
        SearchPhotoFilterDialog
            .newInstance()
            .show(parentFragmentManager, SearchPhotoFilterDialog.TAG)
    }

}