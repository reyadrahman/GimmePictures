package ua.andrii.andrushchenko.gimmepictures.ui.collection.details

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.BottomSheetEditCollectionBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseBottomSheetDialogFragment
import ua.andrii.andrushchenko.gimmepictures.util.compareTextBeforeAndAfter

@AndroidEntryPoint
class EditCollectionDialogFragment :
    BaseBottomSheetDialogFragment<BottomSheetEditCollectionBinding>(BottomSheetEditCollectionBinding::inflate) {

    private val viewModel: CollectionDetailsViewModel by viewModels(
        ownerProducer = { requireParentFragment().childFragmentManager.primaryNavigationFragment!! }
    )
    private val args: EditCollectionDialogFragmentArgs by navArgs()

    private var somethingChanged: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            collectionNameTextInputLayout.editText?.apply {
                compareTextBeforeAndAfter { isChanged ->
                    somethingChanged = isChanged
                }
                setText(args.collection.title)
            }
            collectionDescriptionTextInputLayout.editText?.apply {
                compareTextBeforeAndAfter { isChanged ->
                    somethingChanged = isChanged
                }
                setText(args.collection.description)
            }
            checkboxMakePrivate.apply {
                isChecked = args.collection.private == true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != args.collection.private) {
                        somethingChanged = true
                    }
                }
            }
            btnSave.setOnClickListener { saveCollectionDetails() }
            btnDelete.setOnClickListener { toggleDeleteCollectionDialog(isVisible = true) }

            deleteNoCollectionButton.setOnClickListener { toggleDeleteCollectionDialog(isVisible = false) }
            deleteYesCollectionButton.setOnClickListener {
                viewModel.deleteCollection(args.collection.id)
                dismiss()
            }
        }
    }

    private fun toggleDeleteCollectionDialog(isVisible: Boolean) = with(binding) {
        btnDelete.visibility = if (isVisible) View.GONE else View.VISIBLE
        btnSave.visibility = if (isVisible) View.GONE else View.VISIBLE

        collectionNameTextInputLayout.isEnabled = !isVisible
        collectionDescriptionTextInputLayout.isEnabled = !isVisible
        checkboxMakePrivate.isEnabled = !isVisible

        areYouSureTextView.visibility = if (isVisible) View.VISIBLE else View.GONE
        deleteNoCollectionButton.visibility = if (isVisible) View.VISIBLE else View.GONE
        deleteYesCollectionButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun isInputValid(): Boolean {
        val name = binding.collectionNameTextInputLayout.editText?.text.toString()
        val description = binding.collectionDescriptionTextInputLayout.editText?.text.toString()
        return name.isNotBlank() && name.length <= 60 && description.length <= 250
    }

    private fun showErrorMessage() = with(binding) {
        if (collectionNameTextInputLayout.editText?.text.toString().isBlank()) {
            collectionNameTextInputLayout.error = getString(R.string.collection_name_required)
            collectionNameTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
                if (collectionNameTextInputLayout.error.toString().isNotBlank() && text?.isBlank() != true) {
                    collectionNameTextInputLayout.error = null
                }
            }
        }
    }

    private fun saveCollectionDetails() = with(binding) {
        if (somethingChanged) {
            if (isInputValid()) {
                viewModel.updateCollection(
                    args.collection.id,
                    collectionNameTextInputLayout.editText?.text.toString(),
                    collectionDescriptionTextInputLayout.editText?.text.toString(),
                    checkboxMakePrivate.isChecked
                )
                dismiss()
            } else {
                showErrorMessage()
            }
        } else {
            dismiss()
        }
    }
}