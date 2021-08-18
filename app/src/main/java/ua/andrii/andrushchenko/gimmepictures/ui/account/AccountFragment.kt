package ua.andrii.andrushchenko.gimmepictures.ui.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentAccountBinding
import ua.andrii.andrushchenko.gimmepictures.ui.auth.AuthActivity
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.ui.settings.SettingsActivity
import ua.andrii.andrushchenko.gimmepictures.util.loadImage
import ua.andrii.andrushchenko.gimmepictures.util.showAlertDialog
import ua.andrii.andrushchenko.gimmepictures.util.toast

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(FragmentAccountBinding::inflate) {

    private val viewModel: AccountViewModel by viewModels()

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.notifyUserAuthorizationSuccessful()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        with(binding) {
            viewModel.isUserAuthorized.observe(viewLifecycleOwner) { isAuthorized ->
                toggleUserActionsPanel(isAuthorized)
                if (isAuthorized) {
                    setupUserInfo()
                }
            }

            btnLogin.setOnClickListener {
                Intent(requireContext(), AuthActivity::class.java).also {
                    resultLauncher.launch(it)
                }
            }

            btnShowProfile.setOnClickListener {
                viewModel.userNickname?.let {
                    val direction = AccountFragmentDirections.actionNavAccountToUserDetailsFragment(
                        user = null, username = it
                    )
                    findNavController().navigate(direction)
                }
            }

            btnEditProfile.setOnClickListener {
                val direction = AccountFragmentDirections.actionNavAccountToEditProfileDialog()
                findNavController().navigate(direction)
            }

            btnLogout.setOnClickListener {
                logout()
            }

            btnSettings.setOnClickListener {
                Intent(requireContext(), SettingsActivity::class.java).also {
                    startActivity(it)
                }
            }

            btnAbout.setOnClickListener {
                requireContext().toast(R.string.not_implemented)
            }
        }
    }

    private fun setupUserInfo() = with(binding) {
        userImageView.loadImage(url = viewModel.userProfilePhotoUrl)
        txtUsername.text = getString(
            R.string.user_full_name_with_nickname_formatted,
            viewModel.userFirstName,
            viewModel.userLastName,
            viewModel.userNickname
        )
        viewModel.userEmail?.let {
            txtUserEmail.apply {
                visibility = View.VISIBLE
                text = it
            }
        }
    }

    private fun toggleUserActionsPanel(isVisible: Boolean) {
        with(binding) {
            btnLogin.visibility = if (isVisible) View.GONE else View.VISIBLE
            txtLoginHint.visibility = if (isVisible) View.GONE else View.VISIBLE
            userContainer.visibility = if (isVisible) View.VISIBLE else View.GONE
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
        binding.toolbar.apply {
            setupWithNavController(navController, appBarConfiguration)
            title = getString(R.string.account_and_settings)
        }
    }

    private fun logout() {
        requireContext().showAlertDialog(
            title = R.string.logout,
            message = R.string.logout_confirmation
        ) { _, _ -> viewModel.logout() }
    }
}