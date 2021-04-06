package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotoDetailsBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.ui.auth.AuthActivity
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.util.*
import ua.andrii.andrushchenko.gimmepictures.util.customtabs.CustomTabsHelper
import ua.andrii.andrushchenko.gimmepictures.worker.DownloadWorker

@AndroidEntryPoint
class PhotoDetailsFragment :
    BaseFragment<FragmentPhotoDetailsBinding>(FragmentPhotoDetailsBinding::inflate) {

    private val viewModel: PhotoDetailsViewModel by viewModels()
    private val args: PhotoDetailsFragmentArgs by navArgs()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_photos,
                    R.id.nav_collections,
                    R.id.nav_account
                )
            )
            toolbar.setupWithNavController(navController, appBarConfiguration)
            btnRetry.setOnClickListener { viewModel.getPhotoDetails(args.photoId) }

            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout.bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            // Request data only once
            if (savedInstanceState == null) {
                viewModel.getPhotoDetails(args.photoId)
            }

            viewModel.error.observe(viewLifecycleOwner) {
                toggleErrorLayout(it)
            }

            viewModel.photo.observe(viewLifecycleOwner) {
                setupPhotoDetails(it)
            }
        }
    }

    private fun toggleErrorLayout(isVisible: Boolean) {
        with(binding) {
            contentPhotoDetails.visibility = if (isVisible) View.GONE else View.VISIBLE
            layoutError.visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    private fun setupPhotoDetails(photo: Photo) {
        with(binding) {
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_open_in_browser -> {
                        openPhotoInBrowser(photo.links?.html)
                        true
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }

            // Setup user
            photo.user?.let { user ->
                userImageView.apply {
                    loadImage(
                        url = user.profileImage?.medium,
                        placeholderColorDrawable = null
                    )
                    setOnClickListener {
                        user.username?.let {
                            val direction =
                                PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToUserDetailsFragment(
                                    it)
                            findNavController().navigate(direction)
                        }
                    }
                }
                userTextView.apply {
                    text = user.name ?: "Unknown"
                    setOnClickListener {
                        user.username?.let {
                            val direction =
                                PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToUserDetailsFragment(
                                    it)
                            findNavController().navigate(direction)
                        }
                    }
                }
            }

            photo.location?.let { location ->
                val locationString = when {
                    location.city != null && location.country != null ->
                        getString(R.string.location_template, location.city, location.country)
                    location.city != null && location.country == null -> location.city
                    location.city == null && location.country != null -> location.country
                    else -> null
                }
                locationText.apply {
                    visibility =
                        if (locationString.isNullOrBlank()) View.GONE else View.VISIBLE
                    text = locationString
                    setOnClickListener {
                        openLocationInMaps(
                            location.position?.latitude,
                            location.position?.longitude
                        )
                    }
                }
            }

            btnPhotoInfo.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            // Load photo
            photoImageView.loadImage(
                url = photo.urls.regular,
                placeholderColorDrawable = ColorDrawable(Color.parseColor(photo.color))
            )

            photo.description?.let { description ->
                bottomSheetLayout.descriptionTextView.apply {
                    visibility = View.VISIBLE
                    text = description
                    setOnClickListener { showDescriptionDetailed(description) }
                }
            }

            setLikeButtonState(photo.liked_by_user)
            btnLike.setOnClickListener {
                if (viewModel.isUserAuthorized) {
                    if (photo.liked_by_user) {
                        viewModel.unlikePhoto(photo.id)
                    } else {
                        viewModel.likePhoto(photo.id)
                    }
                    photo.liked_by_user = photo.liked_by_user.not()
                    setLikeButtonState(photo.liked_by_user)
                } else {
                    Toast.makeText(requireContext(),
                        String.format(getString(R.string.login_prompt),
                            getString(R.string.like_photo)),
                        Toast.LENGTH_SHORT).show()
                    Intent(requireContext(), AuthActivity::class.java).also {
                        startActivity(it)
                    }
                }
            }

            btnShare.setOnClickListener {
                sharePhoto(photo.links?.html, photo.description)
            }

            btnDownload.setOnClickListener {
                if (requireContext().fileExists(photo.fileName)) {
                    MaterialAlertDialogBuilder(requireContext()).run {
                        setTitle(R.string.file_exists_title)
                        setMessage(R.string.file_exists_msg)
                        setPositiveButton(R.string.yes) { _, _ -> downloadPhoto(photo) }
                        setNegativeButton(R.string.no, null)
                        show()
                    }
                } else {
                    downloadPhoto(photo)
                }
            }

            photo.tags?.let { tagList ->
                recyclerViewTags.apply {
                    visibility = View.VISIBLE
                    layoutManager = LinearLayoutManager(
                        context,
                        RecyclerView.HORIZONTAL,
                        false
                    ).apply {
                        addItemDecoration(
                            LinearLayoutSpacingDecoration(
                                margin = resources.getDimensionPixelSize(R.dimen.indent_8dp),
                                orientation = RecyclerView.HORIZONTAL
                            )
                        )
                    }
                    adapter = PhotoTagAdapter(object : PhotoTagAdapter.OnTagClickListener {
                        override fun onTagClicked(tag: String) {
                            val direction = PhotoDetailsFragmentDirections
                                .actionPhotoDetailsFragmentToSearchFragment(searchQuery = tag)
                            findNavController().navigate(direction)
                        }
                    }).apply {
                        submitList(tagList)
                    }
                }
            }

            bottomSheetLayout.txtViews.text = "${photo.views?.toAmountReadableString() ?: 0}"
            bottomSheetLayout.txtLikes.text = "${photo.likes?.toAmountReadableString() ?: 0}"
            bottomSheetLayout.txtDownloads.text =
                "${photo.downloads?.toAmountReadableString() ?: 0}"

            bottomSheetLayout.recyclerViewExif.adapter =
                PhotoExifAdapter(requireContext()).apply { setExif(photo) }
        }
    }

    private fun setLikeButtonState(likedByUser: Boolean) {
        binding.btnLike.setImageResource(
            if (likedByUser) R.drawable.ic_like else R.drawable.ic_like_outlined
        )
    }

    private fun downloadPhoto(photo: Photo) {
        if (hasWritePermission()) {
            val sizeOptions = enumValues<PhotoSize>().map { getString(it.stringId) }.toTypedArray()
            MaterialAlertDialogBuilder(requireContext()).run {
                setTitle(getString(R.string.select_image_quality))
                setItems(sizeOptions) { dialog, which ->
                    val photoSize = when (which) {
                        0 -> PhotoSize.RAW
                        1 -> PhotoSize.FULL
                        2 -> PhotoSize.REGULAR
                        3 -> PhotoSize.SMALL
                        4 -> PhotoSize.THUMB
                        else -> PhotoSize.REGULAR
                    }
                    val url = getPhotoUrl(photo, photoSize)
                    viewModel.downloadWorkUUID =
                        DownloadWorker.enqueueDownload(requireContext(),
                            url,
                            photo.fileName,
                            photo.id)
                    dialog.dismiss()
                }
                create()
                show()
            }
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 0)
        }
    }

    private fun sharePhoto(photoLink: String?, photoDescription: String?) {
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, photoLink)
            putExtra(Intent.EXTRA_TITLE, photoDescription)
        }, null)
        startActivity(share)
    }

    private fun openPhotoInBrowser(photoLink: String?) {
        CustomTabsHelper.openCustomTab(requireContext(), Uri.parse(photoLink))
    }

    private fun openLocationInMaps(latitude: Double?, longitude: Double?) {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.resolveActivity(requireContext().packageManager)?.let {
            startActivity(mapIntent)
        } ?: Toast.makeText(requireContext(),
            getString(R.string.gmaps_does_not_installed),
            Toast.LENGTH_SHORT).show()
    }

    private fun showDescriptionDetailed(description: String) {
        MaterialAlertDialogBuilder(requireContext()).setMessage(description).show()
    }
}