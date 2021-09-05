package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
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
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import ua.andrii.andrushchenko.gimmepictures.domain.Tag
import ua.andrii.andrushchenko.gimmepictures.domain.User
import ua.andrii.andrushchenko.gimmepictures.ui.auth.AuthActivity
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.util.*
import ua.andrii.andrushchenko.gimmepictures.util.FragmentCommunicationConstants.ADD_TO_COLLECTION_REQUEST_KEY
import ua.andrii.andrushchenko.gimmepictures.util.FragmentCommunicationConstants.NEW_COLLECTION_IDS

@AndroidEntryPoint
class PhotoDetailsFragment : BaseFragment<FragmentPhotoDetailsBinding>(
    FragmentPhotoDetailsBinding::inflate
) {

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
            setupToolbar()
            btnRetry.setOnClickListener { viewModel.getPhotoDetails(args.photoId) }

            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout.bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            // Since savedInstanceState is ALWAYS null
            // we need another method to be persuaded that
            // the fragment data was initialized
            // to prevent unnecessary data refreshing
            if (!viewModel.isDataInitialized) {
                viewModel.getPhotoDetails(args.photoId)
                viewModel.isDataInitialized = true
            }

            viewModel.error.observe(viewLifecycleOwner) {
                toggleErrorLayout(it)
            }

            viewModel.photo.observe(viewLifecycleOwner) {
                setupPhotoDetails(it)
            }

            setFragmentResultListener(ADD_TO_COLLECTION_REQUEST_KEY) { _, bundle ->
                val result = bundle.getStringArray(NEW_COLLECTION_IDS)
                viewModel.updateCurrentUserCollectionIds(result?.toMutableList())
            }
        }
    }

    private fun toggleErrorLayout(isVisible: Boolean) = with(binding) {
        contentPhotoDetails.visibility = if (isVisible) View.GONE else View.VISIBLE
        layoutError.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun setupPhotoDetails(photo: Photo) = with(binding) {
        setupToolbarWithPhotoLinks(photo.links)

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
                requireContext().showAlertDialog(
                    R.string.file_exists_title,
                    R.string.file_exists_msg
                ) { _, _ -> downloadPhoto(photo) }
            } else {
                downloadPhoto(photo)
            }
        }

        viewModel.currentUserCollectionIds.observe(viewLifecycleOwner) {
            setBookmarkButtonState(it.isNullOrEmpty())
        }

        setupBookmarkButton(photo.id)
        setupTags(photo.tags)
        setupPhotoInfo(photo)
    }

    private fun setupUser(user: User?) = with(binding) {
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

    private fun setupLocation(location: Photo.Location?) = with(binding) {
        location?.let { location ->
            val locationString = when {
                location.city != null && location.country != null ->
                    getString(R.string.location_template, location.city, location.country)
                location.city != null && location.country == null -> location.city
                location.city == null && location.country != null -> location.country
                else -> null
            }
            locationText.apply {
                visibility = if (locationString.isNullOrBlank()) View.GONE else View.VISIBLE
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

    private fun setupDescription(description: String?) = with(binding) {
        description?.let { description ->
            bottomSheetLayout.descriptionTextView.apply {
                visibility = View.VISIBLE
                text = description
                setOnClickListener { showDescriptionDetailed(description) }
            }
        }
    }

    private fun setupLikeButton(photo: Photo) = with(binding) {
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
                requireContext().toast(
                    String.format(
                        getString(R.string.login_prompt),
                        getString(R.string.like_photo)
                    )
                )
                Intent(requireContext(), AuthActivity::class.java).also {
                    resultLauncher.launch(it)
                }
            }
        }
    }

    private fun setLikeButtonState(likedByUser: Boolean) = binding.btnLike.setImageResource(
        if (likedByUser) R.drawable.ic_like else R.drawable.ic_like_outlined
    )

    private fun setupBookmarkButton(photoId: String) {
        binding.btnBookmark.setOnClickListener {
            if (viewModel.isUserAuthorized) {
                val direction =
                    PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToAddToCollectionDialog(
                        photoId,
                        viewModel.currentUserCollectionIds.value?.toTypedArray()
                    )
                findNavController().navigate(direction)
            } else {
                requireContext().toast(
                    String.format(
                        getString(R.string.login_prompt),
                        getString(R.string.save_photo)
                    )
                )
                Intent(requireContext(), AuthActivity::class.java).also {
                    resultLauncher.launch(it)
                }
            }
        }
    }

    private fun setBookmarkButtonState(isCurrentUserCollectionIdsEmpty: Boolean) =
        binding.btnBookmark.setImageResource(
            if (isCurrentUserCollectionIdsEmpty) R.drawable.ic_bookmark_outlined
            else R.drawable.ic_bookmark_filled
        )

    private fun setupTags(tags: List<Tag?>?) {
        tags?.let { tagList ->
            binding.recyclerViewTags.apply {
                visibility = View.VISIBLE
                setHasFixedSize(true)
                setupLinearLayoutManager(
                    margin = resources.getDimensionPixelSize(R.dimen.indent_8dp),
                    recyclerViewOrientation = RecyclerView.HORIZONTAL
                )
                adapter = PhotoTagAdapter { tag ->
                    val direction =
                        PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToSearchFragment(
                            searchQuery = tag
                        )
                    findNavController().navigate(direction)
                }.apply {
                    submitList(tagList)
                }
            }
        }
    }

    private fun setupPhotoInfo(photo: Photo) {
        binding.bottomSheetLayout.apply {
            txtViews.text = "${photo.views?.toReadableString() ?: 0}"
            txtLikes.text = "${photo.likes?.toReadableString() ?: 0}"
            txtDownloads.text = "${photo.downloads?.toReadableString() ?: 0}"

            recyclerViewExif.adapter = PhotoExifAdapter(requireContext()).apply { setExif(photo) }
        }
    }

    private fun downloadPhoto(photo: Photo) {
        if (hasWritePermission()) {
            val direction =
                PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToDownloadDialog(
                    photo
                )
            findNavController().navigate(direction)
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

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_photos,
                R.id.nav_collections,
                R.id.nav_account
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupToolbarWithPhotoLinks(photoLinks: Photo.Links?) {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_open_in_browser -> {
                    openPhotoInBrowser(photoLinks?.html)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }
}