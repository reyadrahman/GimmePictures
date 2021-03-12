package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotoDetailsBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.util.*
import ua.andrii.andrushchenko.gimmepictures.util.customtabs.CustomTabsHelper
import ua.andrii.andrushchenko.gimmepictures.util.recyclerview.RecyclerViewSpacingItemDecoration
import ua.andrii.andrushchenko.gimmepictures.worker.DownloadWorker

@AndroidEntryPoint
class PhotoDetailsFragment : Fragment() {

    private val viewModel by viewModels<PhotoDetailsViewModel>()
    private val args by navArgs<PhotoDetailsFragmentArgs>()

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRetry.setOnClickListener {
            viewModel.getPhotoDetails(args.photoId)
        }

        viewModel.getPhotoDetails(args.photoId)
        viewModel.result.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    displayErrorMsg(isDisplayed = false)
                    displayProgressBar(isDisplayed = true)
                }
                is Result.Success -> {
                    displayProgressBar(isDisplayed = false)
                    displayErrorMsg(isDisplayed = false)
                    displayPhotoDetails(result.value)
                }
                is Result.NetworkError -> {
                    displayProgressBar(isDisplayed = false)
                    displayErrorMsg(isDisplayed = true)
                }
                is Result.Error -> {
                    displayProgressBar(isDisplayed = false)
                    displayErrorMsg(isDisplayed = true)
                }
            }
        }
    }

    private fun displayPhotoDetails(photo: Photo) {
        with(binding) {
            photoImageView.doOnApplyWindowInsets { view, _, _ -> view.updatePadding(top = 0) }

            toolbar.apply {
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_share -> {
                            sharePhoto(photo.links?.html, photo.description)
                            true
                        }
                        R.id.action_open_in_browser -> {
                            openPhotoInBrowser(photo.links?.html)
                            true
                        }
                        else -> super.onOptionsItemSelected(item)
                    }
                }

                val navController = findNavController()
                val appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_photos,
                        R.id.nav_collections,
                        R.id.nav_my_profile
                    )
                )
                setupWithNavController(navController, appBarConfiguration)
            }

            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout.bottomSheet)
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> toolbar.visibility = View.GONE
                        else -> toolbar.visibility = View.VISIBLE
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    bottomSheetLayout.btnBottomSheetExpandCollapse.rotation = -180f * slideOffset
                }
            })

            bottomSheetLayout.btnBottomSheetExpandCollapse.setOnClickListener {
                bottomSheetBehavior.state =
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                        BottomSheetBehavior.STATE_COLLAPSED
                    else
                        BottomSheetBehavior.STATE_EXPANDED
            }

            photoImageView.setOnClickListener {
                when (bottomSheetBehavior.state) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    else -> {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }

            // Load photo
            Glide.with(requireContext())
                .load(photo.urls.regular)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(ColorDrawable(Color.parseColor(photo.color)))
                .into(photoImageView)

            //bottomSheetLayout.bottomSheetHeader.setBackgroundColor(Color.parseColor(photo.color))

            photo.description?.let { description ->
                bottomSheetLayout.descriptionTextView.apply {
                    visibility = View.VISIBLE
                    text = description
                    setOnClickListener { showDescriptionDetailed(description) }
                }
            }

            setLikeButtonState(photo.liked_by_user)
            bottomSheetLayout.btnLike.setOnClickListener {
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
                    val direction =
                        PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToAuthActivity()
                    findNavController().navigate(direction)
                }
            }

            bottomSheetLayout.btnDownload.setOnClickListener {
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

            bottomSheetLayout.txtViews.text = "${photo.views?.toAmountReadableString() ?: 0}"
            bottomSheetLayout.txtLikes.text = "${photo.likes?.toAmountReadableString() ?: 0}"
            bottomSheetLayout.txtDownloads.text =
                "${photo.downloads?.toAmountReadableString() ?: 0}"

            // Load user
            photo.user?.let { user ->
                bottomSheetLayout.userContainer.visibility = View.VISIBLE
                bottomSheetLayout.userContainer.setOnClickListener {
                    val direction =
                        PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToUserDetailsFragment()
                    findNavController().navigate(direction)
                }
                Glide.with(requireContext())
                    .load(user.profileImage?.medium)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_person)
                    .into(bottomSheetLayout.userImageView)
                bottomSheetLayout.userTextView.text = user.name ?: "Unknown"
            }

            photo.location?.let { location ->
                val locationString = when {
                    location.city != null && location.country != null ->
                        getString(R.string.location_template, location.city, location.country)
                    location.city != null && location.country == null -> location.city
                    location.city == null && location.country != null -> location.country
                    else -> null
                }
                bottomSheetLayout.locationText.apply {
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

            bottomSheetLayout.recyclerViewExif.adapter =
                PhotoExifAdapter(requireContext()).apply { setExif(photo) }

            photo.tags?.let { tagList ->
                bottomSheetLayout.recyclerViewTags.apply {
                    visibility = View.VISIBLE
                    layoutManager = LinearLayoutManager(
                        context,
                        RecyclerView.HORIZONTAL,
                        false
                    ).apply {
                        addItemDecoration(
                            RecyclerViewSpacingItemDecoration(
                                context,
                                R.dimen.indent_8dp,
                                RecyclerView.HORIZONTAL
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
        }
    }

    private fun displayProgressBar(isDisplayed: Boolean) {
        with(binding) {
            progressLoading.visibility = if (isDisplayed) View.VISIBLE else View.GONE
            toolbar.visibility = if (!isDisplayed) View.VISIBLE else View.GONE
            photoImageView.visibility = if (!isDisplayed) View.VISIBLE else View.GONE
            bottomSheetLayout.bottomSheet.visibility = if (!isDisplayed) View.VISIBLE else View.GONE
        }
    }

    private fun displayErrorMsg(isDisplayed: Boolean) {
        with(binding) {
            //FIXME: rewrite it cause it weird and ugly
            toolbar.visibility = View.VISIBLE
            layoutError.visibility = if (isDisplayed) View.VISIBLE else View.GONE
            photoImageView.visibility = if (!isDisplayed) View.VISIBLE else View.GONE
            bottomSheetLayout.bottomSheet.visibility = if (!isDisplayed) View.VISIBLE else View.GONE
        }
    }

    private fun setLikeButtonState(likedByUser: Boolean) {
        binding.bottomSheetLayout.btnLike.setImageResource(
            if (likedByUser) R.drawable.ic_like else R.drawable.ic_like_outlined
        )
    }

    private fun downloadPhoto(photo: Photo) {
        if (hasWritePermission(requireContext())) {
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

    /*private fun cancelDownload() {
        viewModel.downloadWorkUUID?.let {
            WorkManager.getInstance(requireContext()).cancelWorkById(it)
        }
    }*/

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

    override fun onStart() {
        super.onStart()
        //requireActivity().setTransparentStatusBar(isTransparent = true)
        requireActivity().transparentStatusBar(isTransparent = true, isFullscreen = false)
    }

    override fun onStop() {
        super.onStop()
        //requireActivity().setTransparentStatusBar(isTransparent = false)
        requireActivity().transparentStatusBar(isTransparent = false, isFullscreen = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}