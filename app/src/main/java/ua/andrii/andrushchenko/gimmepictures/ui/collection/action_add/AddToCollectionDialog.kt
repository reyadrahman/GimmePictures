package ua.andrii.andrushchenko.gimmepictures.ui.collection.action_add

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.BottomSheetAddCollectionBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseBottomSheetDialogFragment
import ua.andrii.andrushchenko.gimmepictures.ui.base.RecyclerViewLoadStateAdapter
import ua.andrii.andrushchenko.gimmepictures.util.BackendResult
import ua.andrii.andrushchenko.gimmepictures.util.FragmentCommunicationConstants.ADD_TO_COLLECTION_REQUEST_KEY
import ua.andrii.andrushchenko.gimmepictures.util.FragmentCommunicationConstants.NEW_COLLECTION_IDS
import ua.andrii.andrushchenko.gimmepictures.util.setupLinearLayoutManager
import ua.andrii.andrushchenko.gimmepictures.util.toast

@AndroidEntryPoint
class AddToCollectionDialog : BaseBottomSheetDialogFragment<BottomSheetAddCollectionBinding>(
    BottomSheetAddCollectionBinding::inflate
) {

    private val viewModel: AddToCollectionViewModel by viewModels()
    private val args: AddToCollectionDialogArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initUserCollectionsIds(args.currentUserCollectionIds?.toMutableList())

        val addToCollectionAdapter =
            AddToCollectionAdapter { collection, selectedStateView, loadingProgress ->
                if (viewModel.currentUserCollectionIds.value?.contains(collection.id) == true) {
                    viewModel.deletePhotoFromCollection(collection.id, args.photoId)
                        .observe(viewLifecycleOwner) {
                            selectedStateView.isClickable = it !is BackendResult.Loading
                            loadingProgress.visibility =
                                if (it is BackendResult.Loading) View.VISIBLE else View.GONE
                            selectedStateView.visibility =
                                if (it is BackendResult.Error) View.VISIBLE else View.GONE
                            if (it is BackendResult.Error) {
                                requireContext().toast(R.string.list_footer_load_error)
                            }

                        }
                } else {
                    viewModel.addPhotoToCollection(collection.id, args.photoId)
                        .observe(viewLifecycleOwner) {
                            selectedStateView.isClickable = it !is BackendResult.Loading
                            loadingProgress.visibility =
                                if (it is BackendResult.Loading) View.VISIBLE else View.GONE
                            selectedStateView.visibility =
                                if (it is BackendResult.Success) View.VISIBLE else View.GONE
                            if (it is BackendResult.Error) {
                                requireContext().toast(R.string.list_footer_load_error)
                            }
                        }
                }
            }

        with(binding) {
            addToCollectionAdapter.addLoadStateListener { loadState ->
                collectionsListingLayout.swipeRefreshLayout.isRefreshing =
                    loadState.refresh is LoadState.Loading
                collectionsListingLayout.recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                collectionsListingLayout.textViewError.isVisible =
                    loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    addToCollectionAdapter.itemCount < 1
                ) {
                    collectionsListingLayout.recyclerView.isVisible = false
                }
            }

            collectionsListingLayout.recyclerView.apply {
                setHasFixedSize(true)
                setupLinearLayoutManager(
                    margin = resources.getDimensionPixelSize(R.dimen.indent_8dp),
                    recyclerViewOrientation = RecyclerView.HORIZONTAL
                )

                adapter = addToCollectionAdapter.withLoadStateHeaderAndFooter(
                    header = RecyclerViewLoadStateAdapter { addToCollectionAdapter.retry() },
                    footer = RecyclerViewLoadStateAdapter { addToCollectionAdapter.retry() }
                )
            }

            viewModel.currentUserCollectionIds.observe(viewLifecycleOwner) {
                addToCollectionAdapter.currentUserCollectionIds = it.orEmpty()
            }

            viewModel.userCollections.observe(viewLifecycleOwner) {
                addToCollectionAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }

            btnCreateCollection.setOnClickListener { showCreateLayout() }

            btnCancel.setOnClickListener {
                showAddLayout()
                resetFields()
            }

            btnCreationDone.setOnClickListener {
                if (isInputValid()) {
                    viewModel.createCollection(
                        collectionNameTextInputLayout.editText?.text.toString(),
                        collectionDescriptionTextInputLayout.editText?.text.toString(),
                        checkboxMakePrivate.isChecked,
                        args.photoId
                    ).observe(viewLifecycleOwner) {
                        when (it) {
                            is BackendResult.Loading -> {
                                layoutProgress.visibility = View.VISIBLE
                            }
                            is BackendResult.Success -> {
                                layoutProgress.visibility = View.GONE
                                collectionsListingLayout.recyclerView.scrollToPosition(0)
                                showAddLayout()
                                addToCollectionAdapter.refresh()
                                resetFields()
                            }
                            is BackendResult.Error -> {
                                layoutProgress.visibility = View.GONE
                                requireContext().toast(R.string.list_footer_load_error)
                            }
                        }
                    }
                } else {
                    showErrorMessage()
                }
            }
        }
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
                if (collectionNameTextInputLayout.error.toString()
                        .isNotBlank() && text?.isBlank() != true
                ) {
                    collectionNameTextInputLayout.error = null
                }
            }
        }
    }

    private fun showAddLayout() = with(binding) {
        addToCollectionLayout.visibility = View.VISIBLE
        createCollectionLayout.visibility = View.INVISIBLE
    }

    private fun showCreateLayout() = with(binding) {
        addToCollectionLayout.visibility = View.INVISIBLE
        createCollectionLayout.visibility = View.VISIBLE
    }

    private fun resetFields() = with(binding) {
        collectionNameTextInputLayout.editText?.setText("")
        collectionNameTextInputLayout.error = null
        collectionDescriptionTextInputLayout.editText?.setText("")
        checkboxMakePrivate.isChecked = false
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val collectionsList = viewModel.currentUserCollectionIds.value?.toTypedArray()
        setFragmentResult(
            ADD_TO_COLLECTION_REQUEST_KEY,
            bundleOf(NEW_COLLECTION_IDS to collectionsList)
        )
        findNavController().popBackStack()
    }
}