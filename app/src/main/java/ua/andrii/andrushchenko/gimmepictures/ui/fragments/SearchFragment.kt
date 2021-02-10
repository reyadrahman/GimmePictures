package ua.andrii.andrushchenko.gimmepictures.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentSearchBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.ui.adapters.PhotosAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.fragments.dialogs.SearchPhotoFilterDialog
import ua.andrii.andrushchenko.gimmepictures.ui.viewmodels.SearchViewModel

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val args by navArgs<SearchFragmentArgs>()
    private val viewModel by hiltNavGraphViewModels<SearchViewModel>(R.id.nav_photos)

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        binding.apply {
            fabFilter.setOnClickListener {
                showPhotoFilterDialog()
            }

            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.search_category_array,
                android.R.layout.simple_dropdown_item_1line
            ).also { arrayAdapter ->
                //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSearchCategory.adapter = arrayAdapter
            }

            spinnerSearchCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }

            val adapter = PhotosAdapter(object : PhotosAdapter.OnItemClickListener {
                override fun onPhotoClick(photo: Photo) {
                    val direction =
                        SearchFragmentDirections.actionSearchFragmentToPhotoDetailsFragment(photo.id)
                    findNavController().navigate(direction)
                }

                override fun onUserClick(user: User) {

                }
            })
            recyclerView.adapter = adapter

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
            false
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}