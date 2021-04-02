package ua.andrii.andrushchenko.gimmepictures.ui.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentAccountBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.util.loadImage

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(FragmentAccountBinding::inflate) {

    private val viewModel: AccountViewModel by viewModels()

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
                    userImageView.loadImage(
                        url = viewModel.userProfilePhotoUrl,
                        placeholderColorDrawable = null
                    )
                    txtUserNickname.text = viewModel.userNickname
                    @SuppressLint("SetTextI18n")
                    txtUsername.text = "${viewModel.userFirstName} ${viewModel.userLastName}"
                    txtEmail.text = viewModel.userEmail
                }
            }

            btnLogin.setOnClickListener {
                val direction = AccountFragmentDirections.actionNavAccountToAuthActivity()
                findNavController().navigate(direction)
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
                val direction =
                    AccountFragmentDirections.actionNavAccountToSettingsFragment()
                findNavController().navigate(direction)
            }

            btnAbout.setOnClickListener {
                Toast.makeText(requireContext(),
                    "Not implemented yet, but it will be soon :)",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleUserActionsPanel(isVisible: Boolean) {
        with(binding) {
            btnLogin.visibility = if (isVisible) View.GONE else View.VISIBLE
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