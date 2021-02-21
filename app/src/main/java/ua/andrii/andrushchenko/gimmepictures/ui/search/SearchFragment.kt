package ua.andrii.andrushchenko.gimmepictures.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentSearchBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.ui.activities.MainActivity
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.photo.PhotosAdapter

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<SearchFragmentArgs>()
    private val viewModel by hiltNavGraphViewModels<SearchViewModel>(R.id.nav_main)

    private val adapter: BasePagedAdapter<Photo> =
        PhotosAdapter(object : PhotosAdapter.OnItemClickListener {
            override fun onPhotoClick(photo: Photo) {
                val direction =
                    SearchFragmentDirections.actionNavSearchToPhotoDetailsFragment(photoId = photo.id)
                findNavController().navigate(direction)
            }

            override fun onUserClick(user: User) {
                val direction =
                    SearchFragmentDirections.actionNavSearchToUsersFragment()
                findNavController().navigate(direction)
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
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
            val appBarConfiguration =
                AppBarConfiguration(setOf(R.id.nav_photos, R.id.nav_collections))
            toolbar.setupWithNavController(navController, appBarConfiguration)

            searchTextInputLayout.editText?.apply {
                setText(args.searchQuery)
                setSelection(text.length)
            }

            searchTextInputLayout.editText?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.updateQuery(searchTextInputLayout.editText?.text.toString())
                    searchTextInputLayout.editText?.clearFocus()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            fabFilter.setOnClickListener {
                val direction =
                    SearchFragmentDirections.actionSearchFragmentToSearchPhotoFilterDialog()
                findNavController().navigate(direction)
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
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).toggleBottomNav(isVisible = false)
    }

    /*override fun onStop() {
        super.onStop()
        val isVisible = when (args.parentDestinationFragmentTag) {
            PhotosFragment.TAG -> true
            PhotoDetailsFragment.TAG -> false
            else -> true
        }
        (requireActivity() as MainActivity).toggleBottomNav(isVisible = isVisible)
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}