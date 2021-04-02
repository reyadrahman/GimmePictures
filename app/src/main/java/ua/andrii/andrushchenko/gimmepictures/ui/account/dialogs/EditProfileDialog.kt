package ua.andrii.andrushchenko.gimmepictures.ui.account.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.databinding.BottomSheetEditMyProfileBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseBottomSheetDialogFragment
import ua.andrii.andrushchenko.gimmepictures.ui.account.AccountViewModel

@AndroidEntryPoint
class EditProfileDialog :
    BaseBottomSheetDialogFragment<BottomSheetEditMyProfileBinding>(BottomSheetEditMyProfileBinding::inflate) {

    private val viewModel: AccountViewModel by viewModels(
        ownerProducer = { requireParentFragment().childFragmentManager.primaryNavigationFragment!! }
    )

    private var somethingChanged: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            usernameTextInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                private var initial: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            firstNameTextInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                private var initial: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            lastNameTextInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                private var initial: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            emailTextInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                private var initial: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            portfolioTextInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                private var initial: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            instagramTextInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                private var initial: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            locationTextInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                private var initial: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            bioTextInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                private var initial: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            with(viewModel) {
                usernameTextInputLayout.editText?.setText(userNickname)
                firstNameTextInputLayout.editText?.setText(userFirstName)
                lastNameTextInputLayout.editText?.setText(userLastName)
                emailTextInputLayout.editText?.setText(userEmail)
                portfolioTextInputLayout.editText?.setText(userPortfolioLink)
                instagramTextInputLayout.editText?.setText(userInstagramUsername)
                locationTextInputLayout.editText?.setText(userLocation)
                bioTextInputLayout.editText?.setText(userBio)
            }

            saveButton.setOnClickListener { dismiss() }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        with(binding) {
            if (somethingChanged) {
                viewModel.updateMyProfile(
                    usernameTextInputLayout.editText?.text.toString(),
                    firstNameTextInputLayout.editText?.text.toString(),
                    lastNameTextInputLayout.editText?.text.toString(),
                    emailTextInputLayout.editText?.text.toString(),
                    portfolioTextInputLayout.editText?.text.toString(),
                    instagramTextInputLayout.editText?.text.toString(),
                    locationTextInputLayout.editText?.text.toString(),
                    bioTextInputLayout.editText?.text.toString()
                )
            }
        }
    }
}