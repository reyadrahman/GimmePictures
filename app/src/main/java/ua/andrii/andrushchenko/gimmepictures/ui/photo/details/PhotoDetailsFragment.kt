package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
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
        binding.apply {

            /*nestedScrollView.doOnApplyWindowInsets { view, _, _ -> view.updatePadding(top = 0) }
            constraintLayout.doOnApplyWindowInsets { view, _, _ -> view.updatePadding(top = 0) }
            photoImageView.doOnApplyWindowInsets { view, _, _ -> view.updatePadding(top = 0) }*/

            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.apply {
                isHideable = false
                isFitToContents = false
                halfExpandedRatio = 0.7F
            }

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
                        R.id.nav_search,
                        R.id.nav_my_profile
                    )
                )
                setupWithNavController(navController, appBarConfiguration)
            }

            // Load photo
            Glide.with(requireContext())
                .load(photo.urls.regular)
                .placeholder(ColorDrawable(Color.parseColor(photo.color)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(ColorDrawable(Color.parseColor(photo.color)))
                .into(photoImageView)

            photo.description?.let { description ->
                descriptionTextView.apply {
                    visibility = View.VISIBLE
                    text = description
                    setOnClickListener { showDescriptionDetailed(description) }
                }
            }

            btnLike.text = "${photo.likes?.toAmountReadableString() ?: 0}"
            setLikeButtonState(photo.liked_by_user)
            btnLike.setOnClickListener {
                if (viewModel.isUserAuthorized) {
                    val likes = if (photo.liked_by_user) {
                        viewModel.unlikePhoto(photo.id)
                        photo.likes
                    } else {
                        viewModel.likePhoto(photo.id)
                        photo.likes?.inc()
                    }
                    btnLike.text = "${likes?.toAmountReadableString() ?: 0}"
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
            btnDownload.text = "${photo.downloads?.toAmountReadableString() ?: 0}"
            txtViews.text = "${photo.views?.toAmountReadableString() ?: 0}"

            // Load user
            photo.user?.let { user ->
                userContainer.visibility = View.VISIBLE
                userContainer.setOnClickListener {
                    val direction =
                        PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToUserDetailsFragment()
                    findNavController().navigate(direction)
                }
                Glide.with(requireContext())
                    .load(user.profileImage?.large)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_person)
                    .into(userImageView)
                userTextView.text = user.name ?: "Unknown"
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

            recyclerViewExif.apply {
                visibility = View.VISIBLE
                adapter =
                    PhotoExifAdapter(requireContext()).apply { setExif(photo) }
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
                                .actionPhotoDetailsFragmentToNavSearch(searchQuery = tag)
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
        binding.apply {
            progressLoading.visibility = if (isDisplayed) View.VISIBLE else View.GONE
            bottomSheet.visibility = if (!isDisplayed) View.VISIBLE else View.GONE
        }
    }

    private fun displayErrorMsg(isDisplayed: Boolean) {
        binding.apply {
            layoutError.visibility = if (isDisplayed) View.VISIBLE else View.GONE
            bottomSheet.visibility = if (!isDisplayed) View.VISIBLE else View.GONE
        }
    }

    private fun setLikeButtonState(likedByUser: Boolean) {
        binding.btnLike.apply {
            if (likedByUser) {
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_like, null)
                setIconTintResource(R.color.red_500)
            } else {
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_like_outlined, null)
                val typedValue = TypedValue()
                requireActivity().theme.resolveAttribute(R.attr.colorControlNormal,
                    typedValue,
                    true)
                setIconTintResource(typedValue.resourceId)
            }
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
        }
    }

    private fun showDescriptionDetailed(description: String) {
        MaterialAlertDialogBuilder(requireContext()).setMessage(description).show()
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTransparentStatusBar(isTransparent = true)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().setTransparentStatusBar(isTransparent = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}