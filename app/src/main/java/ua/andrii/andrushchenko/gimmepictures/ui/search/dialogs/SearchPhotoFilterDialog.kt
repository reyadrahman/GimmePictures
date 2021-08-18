package ua.andrii.andrushchenko.gimmepictures.ui.search.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.data.search.SearchPhotosPagingSource
import ua.andrii.andrushchenko.gimmepictures.databinding.BottomSheetSearchPhotoFilterBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseBottomSheetDialogFragment
import ua.andrii.andrushchenko.gimmepictures.ui.search.SearchViewModel

@AndroidEntryPoint
class SearchPhotoFilterDialog : BaseBottomSheetDialogFragment<BottomSheetSearchPhotoFilterBinding>(
    BottomSheetSearchPhotoFilterBinding::inflate
) {

    private val viewModel: SearchViewModel by viewModels(
        ownerProducer = { requireParentFragment().childFragmentManager.primaryNavigationFragment!! }
    )

    private var searchParametersChanged = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val orderButtonId = when (viewModel.order) {
                SearchPhotosPagingSource.Companion.Order.RELEVANT -> R.id.order_relevance_button
                else -> R.id.order_latest_button
            }
            orderByToggleGroup.apply {
                check(orderButtonId)
                addOnButtonCheckedListener { _, checkedId, isChecked ->
                    if (isChecked) {
                        val order = when (checkedId) {
                            R.id.order_relevance_button -> SearchPhotosPagingSource.Companion.Order.RELEVANT
                            else -> SearchPhotosPagingSource.Companion.Order.LATEST
                        }
                        viewModel.order = order
                        searchParametersChanged = true
                    }
                }
            }

            val contentFilterButtonId = when (viewModel.contentFilter) {
                SearchPhotosPagingSource.Companion.ContentFilter.LOW -> R.id.content_filter_low_button
                else -> R.id.content_filter_high_button
            }
            contentFilterToggleGroup.apply {
                check(contentFilterButtonId)
                addOnButtonCheckedListener { _, checkedId, isChecked ->
                    if (isChecked) {
                        val contentFilter = when (checkedId) {
                            R.id.content_filter_low_button -> SearchPhotosPagingSource.Companion.ContentFilter.LOW
                            else -> SearchPhotosPagingSource.Companion.ContentFilter.HIGH
                        }
                        viewModel.contentFilter = contentFilter
                        searchParametersChanged = true
                    }
                }
            }

            val items = enumValues<SearchPhotosPagingSource.Companion.Color>()
            val titles = items.map { getString(it.titleRes) }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                titles
            )
            val colorFilterDropdownMenu = (colorFilterDropdownMenu.editText as? AutoCompleteTextView)
            colorFilterDropdownMenu?.apply {
                setAdapter(adapter)
                setText(titles[items.indexOf(viewModel.color)], false)
                setOnItemClickListener { _, _, position, _ ->
                    val color = items[position]
                    if (color != viewModel.color) {
                        searchParametersChanged = true
                        viewModel.color = color
                    }
                }
            }

            val orientationButtonId = when (viewModel.orientation) {
                SearchPhotosPagingSource.Companion.Orientation.ANY -> R.id.orientation_any_button
                SearchPhotosPagingSource.Companion.Orientation.PORTRAIT -> R.id.orientation_portrait_button
                SearchPhotosPagingSource.Companion.Orientation.LANDSCAPE -> R.id.orientation_landscape_button
                else -> R.id.orientation_square_button
            }
            orientationToggleGroup.apply {
                check(orientationButtonId)
                addOnButtonCheckedListener { _, checkedId, isChecked ->
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
            }

            btnApply.setOnClickListener { dismiss() }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (searchParametersChanged) {
            viewModel.filterPhotoSearch()
        }
    }
}