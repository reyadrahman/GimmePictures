package ua.andrii.andrushchenko.gimmepictures.ui.user

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentUserDetailsBinding
import ua.andrii.andrushchenko.gimmepictures.domain.User
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.util.CustomTabsHelper
import ua.andrii.andrushchenko.gimmepictures.util.loadImage
import ua.andrii.andrushchenko.gimmepictures.util.toReadableString

@AndroidEntryPoint
class UserDetailsFragment : BaseFragment<FragmentUserDetailsBinding>(
    FragmentUserDetailsBinding::inflate
) {

    private val viewModel: UserDetailsViewModel by viewModels()
    private val args: UserDetailsFragmentArgs by navArgs()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private var isUserInfoEmpty = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarBase()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // Since savedInstanceState is ALWAYS null
        // we need another method to be persuaded that
        // the fragment data was initialized
        // to prevent unnecessary data refreshing
        if (!viewModel.isDataInitialized) {
            when {
                args.user != null -> viewModel.setUser(args.user!!)
                args.username != null -> viewModel.getUserProfile(args.username!!)
                else -> findNavController().navigateUp()
            }
            viewModel.isDataInitialized = true
        }

        binding.btnRetry.setOnClickListener {
            viewModel.refreshUserProfile()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            toggleErrorLayout(isVisible = it)
        }

        viewModel.user.observe(viewLifecycleOwner) {
            displayUserInfo(it)
        }

        setupTabs()
    }

    private fun setupTabs() = with(binding) {
        viewPager.adapter = object : FragmentStateAdapter(this@UserDetailsFragment) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment =
                when (position) {
                    0 -> UserPhotosFragment()
                    1 -> UserLikedPhotosFragment()
                    2 -> UserCollectionsFragment()
                    else -> throw IllegalStateException("PagerAdapter position is not correct $position")
                }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.photos)
                1 -> getString(R.string.user_liked_photos)
                2 -> getString(R.string.collections)
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
    }

    private fun displayUserInfo(user: User) = with(binding) {
        setupToolbarWithUserData(user)

        userImageView.loadImage(url = user.profileImage?.large)

        txtPhotosAmount.text = user.totalPhotos?.toReadableString()
        txtLikesAmount.text = user.totalLikes?.toReadableString()
        txtCollectionsAmount.text = user.totalCollections?.toReadableString()
        txtUsername.text =
            getString(R.string.user_full_name_formatted, user.firstName, user.lastName)
        user.bio?.let { bio ->
            txtUserBio.apply {
                visibility = View.VISIBLE
                text = bio
            }
        }

        isUserInfoEmpty = user.location.isNullOrBlank() &&
                user.instagramUsername.isNullOrBlank() &&
                user.twitterUsername.isNullOrBlank()

        if (!isUserInfoEmpty) {
            bottomSheetLayout.apply {
                userLocation.apply {
                    user.location?.let {
                        visibility = View.VISIBLE
                        text = it
                    }
                }
                userInstagram.apply {
                    user.instagramUsername?.let {
                        visibility = View.VISIBLE
                        text = it
                    }
                }
                userTwitter.apply {
                    user.twitterUsername?.let {
                        visibility = View.VISIBLE
                        text = it
                    }
                }
            }
        }
    }

    private fun toggleErrorLayout(isVisible: Boolean) = with(binding) {
        userHeader.visibility = if (isVisible) View.GONE else View.VISIBLE
        tabLayout.visibility = if (isVisible) View.GONE else View.VISIBLE
        viewPager.visibility = if (isVisible) View.GONE else View.VISIBLE
        layoutError.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun setupToolbarBase() {
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

    private fun setupToolbarWithUserData(user: User) {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_user_profile)

            val userPortfolioItem = menu.findItem(R.id.action_user_portfolio)
            userPortfolioItem.isVisible = !user.portfolioUrl.isNullOrBlank()

            val userMoreInfoItem = menu.findItem(R.id.action_user_more_info)
            userMoreInfoItem.isVisible = !isUserInfoEmpty

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_user_portfolio -> {
                        user.portfolioUrl?.let {
                            CustomTabsHelper.openCustomTab(requireContext(), Uri.parse(it))
                        }
                        true
                    }
                    R.id.action_user_more_info -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        true
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }
            title = user.username
        }
    }
}