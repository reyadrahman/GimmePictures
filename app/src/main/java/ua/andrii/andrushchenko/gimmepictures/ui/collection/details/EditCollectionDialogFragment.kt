package ua.andrii.andrushchenko.gimmepictures.ui.collection.details

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.databinding.BottomSheetEditCollectionBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseBottomSheetDialogFragment

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
                addTextChangedListener(object : TextWatcher {
                    private var initial: String = ""
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                        initial = s.toString()
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        somethingChanged = (s.toString().contentEquals(initial)).not()
                    }
                })
                setText(args.collection.title)
            }
            collectionDescriptionTextInputLayout.editText?.apply {
                addTextChangedListener(object : TextWatcher {
                    private var initial: String = ""
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {
                        initial = s.toString()
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        somethingChanged = (s.toString().contentEquals(initial)).not()
                    }
                })
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

    private fun toggleDeleteCollectionDialog(isVisible: Boolean) {
        with(binding) {
            btnDelete.visibility = if (isVisible) View.GONE else View.VISIBLE
            btnSave.visibility = if (isVisible) View.GONE else View.VISIBLE
            collectionNameTextInputLayout.visibility = if (isVisible) View.GONE else View.VISIBLE
            collectionDescriptionTextInputLayout.visibility =
                if (isVisible) View.GONE else View.VISIBLE
            checkboxMakePrivate.visibility = if (isVisible) View.GONE else View.VISIBLE

            areYouSureTextView.visibility = if (isVisible) View.VISIBLE else View.GONE
            deleteNoCollectionButton.visibility = if (isVisible) View.VISIBLE else View.GONE
            deleteYesCollectionButton.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    private fun saveCollectionDetails() {
        with(binding) {
            if (somethingChanged) {
                viewModel.updateCollection(args.collection.id,
                    collectionNameTextInputLayout.editText?.text.toString(),
                    collectionDescriptionTextInputLayout.editText?.text.toString(),
                    checkboxMakePrivate.isChecked
                )
            }
        }
        dismiss()
    }
}