package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotoDetailsBinding
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo
import ua.andrii.andrushchenko.gimmepictures.domain.entities.User
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

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.notifyUserAuthorizationSuccessful()
            }
        }

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

    private fun toggleErrorLayout(isVisible: Boolean) = with(binding) {
        contentPhotoDetails.visibility = if (isVisible) View.GONE else View.VISIBLE
        layoutError.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun setupPhotoDetails(photo: Photo) = with(binding) {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_open_in_browser -> {
                    openPhotoInBrowser(photo.links?.html)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }

        setupUser(photo.user)
        setupLocation(photo.location)

        btnPhotoInfo.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Load photo
        photoImageView.loadImage(
            url = photo.urls.regular,
            placeholderColorDrawable = ColorDrawable(Color.parseColor(photo.color))
        )

        setupDescription(photo.description)
        setupLikeButton(photo)

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

        viewModel.currentUserCollectionIds.observe(viewLifecycleOwner) {
            setBookmarkButtonState(it ?: emptyList())
        }

        setupBookmarkButton(photo.id)
        setupTags(photo.tags)
        setupPhotoInfo(photo)
    }

    private fun setupUser(user: User?) =
        with(binding) {
            user?.let { user ->
                userImageView.apply {
                    loadImage(
                        url = user.profileImage?.medium,
                        placeholderColorDrawable = null
                    )
                    setOnClickListener {
                        val direction =
                            PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToUserDetailsFragment(
                                user = user,
                                username = null
                            )
                        findNavController().navigate(direction)
                    }
                }
                userTextView.apply {
                    text = user.name ?: "Unknown"
                    setOnClickListener {
                        val direction =
                            PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToUserDetailsFragment(
                                user = user,
                                username = null
                            )
                        findNavController().navigate(direction)
                    }
                }
            }
        }

    private fun setupLocation(location: Photo.Location?) =
        with(binding) {
            location?.let { location ->
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
        }


    private fun setupDescription(description: String?) =
        with(binding) {
            description?.let { description ->
                bottomSheetLayout.descriptionTextView.apply {
                    visibility = View.VISIBLE
                    text = description
                    setOnClickListener { showDescriptionDetailed(description) }
                }
            }
        }


    private fun setupLikeButton(photo: Photo) =
        with(binding) {
            setLikeButtonState(photo.likedByUser)
            btnLike.setOnClickListener {
                if (viewModel.isUserAuthorized) {
                    if (photo.likedByUser) {
                        viewModel.dislikePhoto(photo.id)
                    } else {
                        viewModel.likePhoto(photo.id)
                    }
                    photo.likedByUser = photo.likedByUser.not()
                    setLikeButtonState(photo.likedByUser)
                } else {
                    Toast.makeText(
                        requireContext(),
                        String.format(
                            getString(R.string.login_prompt),
                            getString(R.string.like_photo)
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(requireContext(), AuthActivity::class.java).also {
                        resultLauncher.launch(it)
                    }
                }
            }
        }


    private fun setLikeButtonState(likedByUser: Boolean) {
        binding.btnLike.setImageResource(
            if (likedByUser) R.drawable.ic_like else R.drawable.ic_like_outlined
        )
    }

    private fun setupBookmarkButton(photoId: String) =
        with(binding) {
            btnBookmark.setOnClickListener {
                if (viewModel.isUserAuthorized) {
                    val direction =
                        PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToAddToCollectionDialog(
                            photoId
                        )
                    findNavController().navigate(direction)
                } else {
                    Toast.makeText(
                        requireContext(),
                        String.format(
                            getString(R.string.login_prompt),
                            getString(R.string.save_photo)
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                    Intent(requireContext(), AuthActivity::class.java).also {
                        resultLauncher.launch(it)
                    }
                }
            }
        }


    private fun setBookmarkButtonState(currentUserCollectionIds: List<String>) {
        binding.btnBookmark.setImageResource(
            if (currentUserCollectionIds.isNotEmpty()) R.drawable.ic_bookmark_filled
            else R.drawable.ic_bookmark_outlined
        )
    }

    private fun setupTags(tags: List<Photo.Tag?>?) =
        with(binding) {
            tags?.let { tagList ->
                recyclerViewTags.apply {
                    visibility = View.VISIBLE
                    setHasFixedSize(true)
                    setupLinearLayoutManager(
                        margin = resources.getDimensionPixelSize(R.dimen.indent_8dp),
                        recyclerViewOrientation = RecyclerView.HORIZONTAL
                    )
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
        }


    private fun setupPhotoInfo(photo: Photo) =
        with(binding) {
            bottomSheetLayout.txtViews.text = "${photo.views?.toReadableString() ?: 0}"
            bottomSheetLayout.txtLikes.text = "${photo.likes?.toReadableString() ?: 0}"
            bottomSheetLayout.txtDownloads.text =
                "${photo.downloads?.toReadableString() ?: 0}"

            bottomSheetLayout.recyclerViewExif.adapter =
                PhotoExifAdapter(requireContext()).apply { setExif(photo) }
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
                        DownloadWorker.enqueueDownload(
                            requireContext(),
                            url,
                            photo.fileName,
                            photo.id
                        )
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
        val shareIntent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, photoLink)
            putExtra(Intent.EXTRA_TITLE, photoDescription)
        }, null)
        startActivity(shareIntent)
    }

    private fun openPhotoInBrowser(photoLink: String?) {
        CustomTabsHelper.openCustomTab(requireContext(), Uri.parse(photoLink))
    }

    private fun openLocationInMaps(latitude: Double?, longitude: Double?) {
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$latitude,$longitude"))
        startActivity(mapIntent)
    }

    private fun showDescriptionDetailed(description: String) {
        MaterialAlertDialogBuilder(requireContext()).setMessage(description).show()
    }
}