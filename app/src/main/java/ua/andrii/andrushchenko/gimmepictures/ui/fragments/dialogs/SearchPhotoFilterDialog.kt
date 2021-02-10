package ua.andrii.andrushchenko.gimmepictures.ui.fragments.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.source.SearchPhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.databinding.BottomSheetSearchPhotoFilterBinding
import ua.andrii.andrushchenko.gimmepictures.ui.viewmodels.SearchViewModel

@AndroidEntryPoint
class SearchPhotoFilterDialog : BottomSheetDialogFragment() {

    private val viewModel by hiltNavGraphViewModels<SearchViewModel>(R.id.nav_photos)
    private var searchParametersChanged = false

    private var _binding: BottomSheetSearchPhotoFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)

        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet).apply {
                skipCollapsed = true
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSearchPhotoFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderButtonId = when (viewModel.order) {
            SearchPhotosPagingSource.Companion.Order.RELEVANT -> R.id.order_relevance_button
            else -> R.id.order_latest_button
        }

        binding.apply {
            orderByToggleGroup.check(orderButtonId)
            orderByToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    val order = when (checkedId) {
                        R.id.order_relevance_button -> SearchPhotosPagingSource.Companion.Order.RELEVANT
                        else -> SearchPhotosPagingSource.Companion.Order.LATEST
                    }
                    viewModel.order = order
                    searchParametersChanged = true
                }
            }

            val contentFilterButtonId = when (viewModel.contentFilter) {
                SearchPhotosPagingSource.Companion.ContentFilter.LOW -> R.id.content_filter_low_button
                else -> R.id.content_filter_high_button
            }
            contentFilterToggleGroup.check(contentFilterButtonId)
            contentFilterToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    val contentFilter = when (checkedId) {
                        R.id.content_filter_low_button -> SearchPhotosPagingSource.Companion.ContentFilter.LOW
                        else -> SearchPhotosPagingSource.Companion.ContentFilter.HIGH
                    }
                    viewModel.contentFilter = contentFilter
                    searchParametersChanged = true
                }
            }

            val items = enumValues<SearchPhotosPagingSource.Companion.Color>()
            val titles = items.map { getString(it.titleRes) }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                titles
            )
            val colorFilterDropdownMenu =
                (colorFilterDropdownMenu.editText as? AutoCompleteTextView)
            colorFilterDropdownMenu?.setAdapter(adapter)
            colorFilterDropdownMenu?.setText(titles[items.indexOf(viewModel.color)], false)
            colorFilterDropdownMenu?.setOnItemClickListener { _, _, position, _ ->
                val color = items[position]
                if (color != viewModel.color) {
                    searchParametersChanged = true
                    viewModel.color = color
                }
            }

            val orientationButtonId = when (viewModel.orientation) {
                SearchPhotosPagingSource.Companion.Orientation.ANY -> R.id.orientation_any_button
                SearchPhotosPagingSource.Companion.Orientation.PORTRAIT -> R.id.orientation_portrait_button
                SearchPhotosPagingSource.Companion.Orientation.LANDSCAPE -> R.id.orientation_landscape_button
                else -> R.id.orientation_square_button
            }
            orientationToggleGroup.check(orientationButtonId)
            orientationToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    val orientation = when (checkedId) {
                        R.id.orientation_any_button -> SearchPhotosPagingSource.Companion.Orientation.ANY
                        R.id.orientation_portrait_button -> SearchPhotosPagingSource.Companion.Orientation.PORTRAIT
                        R.id.orientation_landscape_button -> SearchPhotosPagingSource.Companion.Orientation.LANDSCAPE
                        else -> SearchPhotosPagingSource.Companion.Orientation.SQUARISH
                    }
                    viewModel.orientation = orientation
                    searchParametersChanged = true
                }
            }

            applyButton.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (searchParametersChanged) {
            viewModel.filterPhotoSearch()
        }
    }

    companion object {
        val TAG: String = SearchPhotoFilterDialog::class.java.simpleName
        fun newInstance() = SearchPhotoFilterDialog()
    }
}