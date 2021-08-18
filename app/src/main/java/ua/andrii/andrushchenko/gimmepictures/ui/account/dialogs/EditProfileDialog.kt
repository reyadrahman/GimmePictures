package ua.andrii.andrushchenko.gimmepictures.ui.account.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.BottomSheetEditMyProfileBinding
import ua.andrii.andrushchenko.gimmepictures.ui.account.AccountViewModel
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseBottomSheetDialogFragment
import ua.andrii.andrushchenko.gimmepictures.util.compareTextBeforeAndAfter
import java.util.regex.Pattern

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
                    after: Int
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val userNicknamePatternMatcher = Pattern.compile("([A-Za-z0-9_]+)")
                    if (!userNicknamePatternMatcher.matcher(s.toString()).matches()) {
                        usernameTextInputLayout.isErrorEnabled = true
                        usernameTextInputLayout.error =
                            getString(R.string.username_description_text)
                        btnSave.isEnabled = false
                    } else {
                        usernameTextInputLayout.isErrorEnabled = false
                        usernameTextInputLayout.error = null
                        btnSave.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            firstNameTextInputLayout.editText?.compareTextBeforeAndAfter { isChanged ->
                somethingChanged = isChanged
            }

            lastNameTextInputLayout.editText?.compareTextBeforeAndAfter { isChanged ->
                somethingChanged = isChanged
            }

            emailTextInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                private var initial: String = ""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    initial = s.toString()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val userEmailPatternMatcher = Pattern.compile(
                        "[a-zA-Z0-9+._%-+]{1,256}" + "@"
                                + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                                + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+"
                    )
                    if (!userEmailPatternMatcher.matcher(s.toString()).matches()) {
                        emailTextInputLayout.isErrorEnabled = true
                        emailTextInputLayout.error = getString(R.string.invalid_email)
                        btnSave.isEnabled = false
                    } else {
                        emailTextInputLayout.isErrorEnabled = false
                        emailTextInputLayout.error = null
                        btnSave.isEnabled = true
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    somethingChanged = (s.toString().contentEquals(initial)).not()
                }
            })

            portfolioTextInputLayout.editText?.compareTextBeforeAndAfter { isChanged ->
                somethingChanged = isChanged
            }

            instagramTextInputLayout.editText?.compareTextBeforeAndAfter { isChanged ->
                somethingChanged = isChanged
            }

            locationTextInputLayout.editText?.compareTextBeforeAndAfter { isChanged ->
                somethingChanged = isChanged
            }

            bioTextInputLayout.editText?.compareTextBeforeAndAfter { isChanged ->
                somethingChanged = isChanged
            }

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

            btnSave.setOnClickListener { dismiss() }
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