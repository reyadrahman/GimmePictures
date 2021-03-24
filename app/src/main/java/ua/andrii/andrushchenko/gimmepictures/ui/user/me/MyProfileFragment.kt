package ua.andrii.andrushchenko.gimmepictures.ui.user.me

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentMyProfileBinding
import ua.andrii.andrushchenko.gimmepictures.models.Me
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.util.BackendCallResult
import ua.andrii.andrushchenko.gimmepictures.util.loadImage
import ua.andrii.andrushchenko.gimmepictures.util.toAmountReadableString

@AndroidEntryPoint
class MyProfileFragment :
    BaseFragment<FragmentMyProfileBinding>(FragmentMyProfileBinding::inflate) {
    private val viewModel: MyProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_photos,
                R.id.nav_collections,
                R.id.nav_my_profile
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        with(viewModel) {
            isUserAuthorized.observe(viewLifecycleOwner) { isAuthorized ->
                if (isAuthorized) {
                    obtainMyProfile()
                    apiCallResult.observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is BackendCallResult.Loading -> {
                                //displayErrorMsg(isDisplayed = false)
                                displayProgressBar(isDisplayed = true)
                            }
                            is BackendCallResult.Success -> {
                                displayProgressBar(isDisplayed = false)
                                //displayErrorMsg(isDisplayed = false)
                                displayUserAccount(result.value)
                            }
                            is BackendCallResult.Error -> {
                                displayProgressBar(isDisplayed = false)
                                //displayErrorMsg(isDisplayed = true)
                            }
                        }
                    }
                } else {
                    val direction = MyProfileFragmentDirections.actionNavMyProfileToAuthActivity()
                    findNavController().navigate(direction)
                }
            }
        }
    }

    private fun displayUserAccount(me: Me) {
        with(binding) {
            toolbar.apply {
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_logout -> {
                            logout()
                            true
                        }
                        R.id.action_settings -> {
                            val direction =
                                MyProfileFragmentDirections.actionNavMyProfileToSettingsFragment()
                            findNavController().navigate(direction)
                            true
                        }
                        else -> super.onOptionsItemSelected(item)
                    }
                }
                title = me.username
            }

            userImageView.loadImage(
                url = me.profileImage?.large,
                placeholderColorDrawable = null
            )

            txtPhotosAmount.text = me.totalPhotos?.toAmountReadableString()
            txtFollowersAmount.text = me.followersCount?.toAmountReadableString()
            txtFollowingAmount.text = me.followingCount?.toAmountReadableString()
            @SuppressLint("SetTextI18n")
            txtUsername.text = "${me.firstName} ${me.lastName}"
        }
    }

    /*private fun displayErrorMsg(isDisplayed: Boolean) {

    }*/

    private fun displayProgressBar(isDisplayed: Boolean) {
        with(binding) {
            progressBar.visibility = if (isDisplayed) View.VISIBLE else View.GONE
            userHeader.visibility = if (isDisplayed) View.GONE else View.VISIBLE
        }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext()).run {
            setTitle(R.string.logout)
            setMessage(R.string.logout_confirmation)
            setPositiveButton(R.string.yes) { _, _ -> viewModel.logout() }
            setNegativeButton(R.string.no, null)
            show()
        }
    }
}