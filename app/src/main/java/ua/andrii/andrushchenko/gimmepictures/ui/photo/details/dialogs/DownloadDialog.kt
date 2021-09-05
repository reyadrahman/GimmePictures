package ua.andrii.andrushchenko.gimmepictures.ui.photo.details.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.databinding.BottomSheetDownloadPhotoBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseBottomSheetDialogFragment
import ua.andrii.andrushchenko.gimmepictures.util.PhotoSize
import ua.andrii.andrushchenko.gimmepictures.util.fileName
import ua.andrii.andrushchenko.gimmepictures.util.getUrlForSize
import ua.andrii.andrushchenko.gimmepictures.worker.DownloadWorker

@AndroidEntryPoint
class DownloadDialog : BaseBottomSheetDialogFragment<BottomSheetDownloadPhotoBinding>(
    BottomSheetDownloadPhotoBinding::inflate
) {
    private lateinit var photoSize: PhotoSize
    private val args: DownloadDialogArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            photoSizeOriginal.setOnClickListener {
                photoSize = PhotoSize.ORIGINAL
                dismiss()
            }
            photoSizeLarge.setOnClickListener {
                photoSize = PhotoSize.LARGE
                dismiss()
            }
            photoSizeMedium.setOnClickListener {
                photoSize = PhotoSize.MEDIUM
                dismiss()
            }
            photoSizeSmall.setOnClickListener {
                photoSize = PhotoSize.SMALL
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (this::photoSize.isInitialized) {
            args.photo.let {
                val url = it.getUrlForSize(photoSize)
                DownloadWorker.enqueueDownload(
                    requireContext(),
                    url,
                    it.fileName,
                    it.id
                )
            }
        }
    }
}