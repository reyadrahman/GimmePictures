package ua.andrii.andrushchenko.gimmepictures.ui.search

import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentSearchBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.util.focusAndShowKeyboard
import ua.andrii.andrushchenko.gimmepictures.util.hideKeyboard
import java.lang.IllegalStateException

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val args: SearchFragmentArgs by navArgs()
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_photos,
                    R.id.nav_collections,
                    R.id.nav_my_profile
                )
            )
            toolbar.setupWithNavController(navController, appBarConfiguration)

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
                        //searchPhotosListingLayout.recyclerView.scrollToPosition(0)
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
        }
    }
}

private class SearchFragmentPagerAdapter(
    private val context: Context,
    private val fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val fragmentTags = SparseArray<String>()

    enum class SearchFragment(val titleRes: Int) {
        PHOTO(R.string.photos),
        COLLECTION(R.string.collections),
        USER(R.string.users)
    }

    //fun getItemType(position: Int) = SearchFragment.values()[position]

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PhotoFragment()
            1 -> CollectionsFragment()
            2 -> UsersFragment()
            else -> throw IllegalStateException("PagerAdapter position is not correct $position")
        }
    }

    /*override fun getItem(position: Int): Fragment {
        return when (getItemType(position)) {
            SearchFragment.PHOTO -> PhotoFragment()
            SearchFragment.COLLECTION -> CollectionsFragment()
            SearchFragment.USER -> UsersFragment()
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        (fragment as? Fragment)?.tag?.let { fragmentTags.put(position, it) }
        return fragment
    }

    override fun getPageTitle(position: Int) = context.getString(getItemType(position).titleRes)

    override fun getCount() = SearchFragment.values().size*/
}

/*val materialShapeDrawable = toolbar.background as MaterialShapeDrawable
            materialShapeDrawable.shapeAppearanceModel =
                materialShapeDrawable.shapeAppearanceModel
                    .toBuilder()
                    .setAllCorners(
                        CornerFamily.ROUNDED,
                        resources.getDimension(R.dimen.indent_16dp)
                    )
                    .build()*/