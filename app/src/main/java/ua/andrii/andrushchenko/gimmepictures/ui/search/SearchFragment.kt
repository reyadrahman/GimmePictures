package ua.andrii.andrushchenko.gimmepictures.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentSearchBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.util.focusAndShowKeyboard
import ua.andrii.andrushchenko.gimmepictures.util.hideKeyboard

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val args: SearchFragmentArgs by navArgs()
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        with(binding) {
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
                        for (tabNum in 0..tabLayout.tabCount) {
                            val fragment = childFragmentManager.findFragmentByTag(
                                "f$tabNum"
                            ) as? BaseRecyclerViewFragment<*, *>
                            fragment?.scrollRecyclerViewToTop()
                        }
                        hideKeyboard() // For searchTextInputLayout.editText
                        return@setOnEditorActionListener true
                    }
                    return@setOnEditorActionListener false
                }

                if (text.isNullOrBlank()) {
                    focusAndShowKeyboard() // For searchTextInputLayout.editText
                }
            }

            viewPager.adapter = object : FragmentStateAdapter(this@SearchFragment) {
                override fun getItemCount(): Int = 3

                override fun createFragment(position: Int): Fragment =
                    when (position) {
                        0 -> PhotosResultFragment()
                        1 -> CollectionsResultFragment()
                        2 -> UsersResultsFragment()
                        else -> throw IllegalStateException("PagerAdapter position is not correct $position")
                    }
            }

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.photos)
                    1 -> getString(R.string.collections)
                    2 -> getString(R.string.users)
                    else -> throw IllegalStateException("PagerAdapter position is not correct $position")
                }
            }.attach()

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {}
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    val fragment = childFragmentManager.findFragmentByTag(
                        "f${viewPager.currentItem}"
                    ) as? BaseRecyclerViewFragment<*, *>
                    fragment?.scrollRecyclerViewToTop()
                }
            })

            viewModel.query.observe(viewLifecycleOwner) { currentQuery ->
                if (viewPager.currentItem == 0 && !currentQuery.isNullOrBlank()) fabFilter.show() else fabFilter.hide()
            }

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == 0 && !viewModel.query.value.isNullOrBlank()) fabFilter.show() else fabFilter.hide()
                }
            })

            fabFilter.setOnClickListener {
                val direction = SearchFragmentDirections.actionSearchFragmentToSearchPhotoFilterDialog()
                findNavController().navigate(direction)
            }
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
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}