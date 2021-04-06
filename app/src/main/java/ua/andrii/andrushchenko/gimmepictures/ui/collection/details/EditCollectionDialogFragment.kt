package ua.andrii.andrushchenko.gimmepictures.ui.collection.details

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
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
            if (savedInstanceState == null) {
                checkboxMakePrivate.isChecked = args.collection.private == true
            }
            btnSave.setOnClickListener { saveCollectionDetails() }
            btnDelete.setOnClickListener { deleteCollection() }
        }
    }

    private fun deleteCollection() {
        // FIXME: temporary solution. Will be replaced by layout confirmation
        MaterialAlertDialogBuilder(requireContext()).run {
            setTitle(R.string.delete)
            setMessage(R.string.are_you_sure)
            setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteCollection(args.collection.id)
                dismiss()
            }
            setNegativeButton(R.string.no, null)
            show()
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