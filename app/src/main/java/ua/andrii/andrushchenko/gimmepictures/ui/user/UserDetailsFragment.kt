package ua.andrii.andrushchenko.gimmepictures.ui.user

import android.annotation.SuppressLint
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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentUserDetailsBinding
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseRecyclerViewFragment
import ua.andrii.andrushchenko.gimmepictures.util.customtabs.CustomTabsHelper
import ua.andrii.andrushchenko.gimmepictures.util.loadImage
import ua.andrii.andrushchenko.gimmepictures.util.toAmountReadableString

@AndroidEntryPoint
class UserDetailsFragment :
    BaseFragment<FragmentUserDetailsBinding>(FragmentUserDetailsBinding::inflate) {

    private val viewModel: UserDetailsViewModel by viewModels()
    private val args: UserDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_photos,
                R.id.nav_collections,
                R.id.nav_account
            )
        )

        if (savedInstanceState == null) {
            viewModel.getUserProfile(args.username)
        }

        with(binding) {
            toolbar.setupWithNavController(navController, appBarConfiguration)
            btnRetry.setOnClickListener {
                viewModel.getUserProfile(args.username)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            toggleErrorLayout(isVisible = it)
        }

        viewModel.user.observe(viewLifecycleOwner) {
            displayUserAccountInfo(it)
        }
    }

    private fun displayUserAccountInfo(user: User) {
        with(binding) {
            toolbar.apply {
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_user_portfolio -> {
                            user.portfolioUrl?.let {
                                CustomTabsHelper.openCustomTab(requireContext(), Uri.parse(it))
                            }
                            true
                        }
                        else -> super.onOptionsItemSelected(item)
                    }
                }
                title = user.username
            }

            userImageView.loadImage(
                url = user.profileImage?.large,
                placeholderColorDrawable = null
            )

            txtPhotosAmount.text = user.totalPhotos?.toAmountReadableString()
            txtFollowersAmount.text = user.followersCount?.toAmountReadableString()
            txtFollowingAmount.text = user.followingCount?.toAmountReadableString()
            @SuppressLint("SetTextI18n")
            txtUsername.text = "${user.firstName} ${user.lastName}"
            /*user.email?.let { email ->
                txtEmail.apply {
                    visibility = View.VISIBLE
                    text = email
                }
            }*/

            viewPager.visibility = View.VISIBLE
            viewPager.adapter = UserContentPagerAdapter(this@UserDetailsFragment)
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
    }

    private fun toggleErrorLayout(isVisible: Boolean) {
        with(binding) {
            userHeader.visibility = if (isVisible) View.GONE else View.VISIBLE
            viewPager.visibility = if (isVisible) View.GONE else View.VISIBLE
            layoutError.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}

private class UserContentPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> UserPhotosFragment()
            1 -> UserLikedPhotosFragment()
            2 -> UserCollectionsFragment()
            else -> throw IllegalStateException("PagerAdapter position is not correct $position")
        }
}