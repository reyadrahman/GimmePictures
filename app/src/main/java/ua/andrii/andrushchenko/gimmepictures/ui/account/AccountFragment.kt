package ua.andrii.andrushchenko.gimmepictures.ui.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentAccountBinding
import ua.andrii.andrushchenko.gimmepictures.ui.auth.AuthActivity
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.ui.settings.SettingsActivity
import ua.andrii.andrushchenko.gimmepictures.util.loadImage

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
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_photos,
                R.id.nav_collections,
                R.id.nav_account
            )
        )

        with(binding) {
            toolbar.setupWithNavController(navController, appBarConfiguration)
            toolbar.title = getString(R.string.account_and_settings)
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
                    val direction =
                        AccountFragmentDirections.actionNavAccountToUserDetailsFragment(it)
                    findNavController().navigate(direction)
                }
            }

            btnEditProfile.setOnClickListener {
                val direction =
                    AccountFragmentDirections.actionNavAccountToEditProfileDialog()
                findNavController().navigate(direction)
            }

            btnLogout.setOnClickListener {
                logout()
            }

            btnSettings.setOnClickListener {
                /*val direction =
                    AccountFragmentDirections.actionNavAccountToSettingsFragment()
                findNavController().navigate(direction)*/
                Intent(requireContext(), SettingsActivity::class.java).also {
                    startActivity(it)
                }
            }

            btnAbout.setOnClickListener {
                Toast.makeText(requireContext(),
                    "Not implemented yet, but it will be soon :)",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUserInfo() {
        with(binding) {
            userImageView.loadImage(
                url = viewModel.userProfilePhotoUrl,
                placeholderColorDrawable = null
            )
            txtUsername.text =
                "${viewModel.userFirstName} ${viewModel.userLastName} (@${viewModel.userNickname})"
            viewModel.userEmail?.let {
                txtEmail.apply {
                    visibility = View.VISIBLE
                    text = viewModel.userEmail
                }
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