package ua.andrii.andrushchenko.gimmepictures.ui.photo.dialogs

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.photos.PhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.databinding.BottomSheetPhotoOrderSelectionBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseBottomSheetDialogFragment
import ua.andrii.andrushchenko.gimmepictures.util.FragmentCommunicationConstants.CHANGE_PHOTOS_ORDER_REQUEST_KEY
import ua.andrii.andrushchenko.gimmepictures.util.FragmentCommunicationConstants.NEW_PHOTOS_ORDER

@AndroidEntryPoint
class PhotoOrderSelectionDialog : BaseBottomSheetDialogFragment<BottomSheetPhotoOrderSelectionBinding>(
    BottomSheetPhotoOrderSelectionBinding::inflate
) {

    private val args: PhotoOrderSelectionDialogArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orderOptions = enumValues<PhotosPagingSource.Companion.Order>()
            .map { getString(it.titleRes) }
            .toTypedArray()

        for (radioButtonTitle in orderOptions) {
            val radioButton = RadioButton(requireContext()).apply {
                id = View.generateViewId()
                text = radioButtonTitle
                setPadding(resources.getDimensionPixelSize(R.dimen.indent_16dp))
            }
            binding.radioGroup.addView(radioButton)
        }

        binding.radioGroup.check(binding.radioGroup.getChildAt(args.currentSelection).id)

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
            setFragmentResult(
                CHANGE_PHOTOS_ORDER_REQUEST_KEY,
                bundleOf(NEW_PHOTOS_ORDER to radioGroup.indexOfChild(radioButton))
            )
            findNavController().popBackStack()
        }
    }
}