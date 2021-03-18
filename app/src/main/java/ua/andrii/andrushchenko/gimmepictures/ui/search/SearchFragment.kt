package ua.andrii.andrushchenko.gimmepictures.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentSearchBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.photo.PhotosAdapter
import ua.andrii.andrushchenko.gimmepictures.util.focusAndShowKeyboard
import ua.andrii.andrushchenko.gimmepictures.util.hideKeyboard
import ua.andrii.andrushchenko.gimmepictures.util.setupStaggeredGridLayoutManager

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val args: SearchFragmentArgs by navArgs()
    private val viewModel: SearchViewModel by viewModels()

    private val pagedAdapter: BasePagedAdapter<Photo> =
        PhotosAdapter(object : PhotosAdapter.OnItemClickListener {
            override fun onPhotoClick(photo: Photo) {
                val direction =
                    SearchFragmentDirections.actionGlobalPhotoDetailsFragment(photoId = photo.id)
                findNavController().navigate(direction)
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            /*val materialShapeDrawable = toolbar.background as MaterialShapeDrawable
            materialShapeDrawable.shapeAppearanceModel =
                materialShapeDrawable.shapeAppearanceModel
                    .toBuilder()
                    .setAllCorners(
                        CornerFamily.ROUNDED,
                        resources.getDimension(R.dimen.indent_16dp)
                    )
                    .build()*/

            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_photos,
                    R.id.nav_collections,
                    R.id.nav_my_profile
                )
            )
            toolbar.setupWithNavController(navController, appBarConfiguration)

            val selectedCategoryId = when (viewModel.selectedCategory) {
                SearchViewModel.SearchCategory.PHOTOS -> R.id.toggle_photos
                SearchViewModel.SearchCategory.COLLECTIONS -> R.id.toggle_collections
                else -> R.id.toggle_users
            }

            searchForToggleGroup.check(selectedCategoryId)

            searchTextInputLayout.editText?.apply {
                if (viewModel.query.value.isNullOrBlank()) {
                    val searchQuery = args.searchQuery
                    if (!searchQuery.isNullOrBlank()) {
                        setText(searchQuery)
                        viewModel.updateQuery(searchQuery)
                    }
                } else {
                    setText(viewModel.query.value)
                }
                setSelection(text.length)

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        viewModel.updateQuery(searchTextInputLayout.editText?.text.toString())
                        searchPhotosListingLayout.recyclerView.scrollToPosition(0)
                        searchTextInputLayout.editText?.hideKeyboard()
                        return@setOnEditorActionListener true
                    }
                    return@setOnEditorActionListener false
                }

                if (text.isNullOrBlank()) {
                    focusAndShowKeyboard()
                }
            }

            fabFilter.setOnClickListener {
                val direction =
                    SearchFragmentDirections.actionSearchFragmentToSearchPhotoFilterDialog()
                findNavController().navigate(direction)
            }

            searchPhotosListingLayout.swipeRefreshLayout.setOnRefreshListener {
                pagedAdapter.refresh()
            }

            pagedAdapter.addLoadStateListener { loadState ->
                searchPhotosListingLayout.swipeRefreshLayout.isRefreshing =
                    loadState.refresh is LoadState.Loading
                searchPhotosListingLayout.recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                searchPhotosListingLayout.textViewError.isVisible =
                    loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    pagedAdapter.itemCount < 1
                ) {
                    searchPhotosListingLayout.recyclerView.isVisible = false
                    searchPhotosListingLayout.textViewEmpty.isVisible = true
                } else {
                    searchPhotosListingLayout.textViewEmpty.isVisible = false
                }
            }

            searchPhotosListingLayout.recyclerView.apply {
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

            viewModel.photoResults.observe(viewLifecycleOwner) {
                pagedAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}