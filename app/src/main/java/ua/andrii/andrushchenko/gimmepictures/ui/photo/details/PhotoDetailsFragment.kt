package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotoDetailsBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.ui.activities.MainActivity
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
                    Toast.makeText(requireContext(), "IOException", Toast.LENGTH_SHORT).show()
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
            nestedScrollView.doOnApplyWindowInsets { view, _, _ -> view.updatePadding(top = 0) }
            constraintLayout.doOnApplyWindowInsets { view, _, _ -> view.updatePadding(top = 0) }
            photoImageView.doOnApplyWindowInsets { view, _, _ -> view.updatePadding(top = 0) }

            toolbar.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_share) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.share),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            }

            val navController = findNavController()
            val appBarConfiguration =
                AppBarConfiguration(setOf(R.id.nav_photos, R.id.nav_collections))
            toolbar.setupWithNavController(navController, appBarConfiguration)

            photo.location?.let { location ->
                val locationString = when {
                    location.city != null && location.country != null ->
                        getString(R.string.location_template, location.city, location.country)
                    location.city != null && location.country == null -> location.city
                    location.city == null && location.country != null -> location.country
                    else -> null
                }
                locationText.visibility =
                    if (locationString.isNullOrBlank()) View.GONE else View.VISIBLE
                locationText.text = locationString
            }

            // Load photo
            Glide.with(requireContext())
                .load(photo.urls.regular)
                .placeholder(ColorDrawable(Color.parseColor(photo.color)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(ColorDrawable(Color.parseColor(photo.color)))
                .into(photoImageView)

            // Load user
            photo.user?.let { user ->
                userContainer.isVisible = true
                userContainer.setOnClickListener {
                    val direction =
                        PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToUsersFragment()
                    findNavController().navigate(direction)
                }
                Glide.with(requireContext())
                    .load(user.profileImage?.small)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_person)
                    .into(userImageView)
                userTextView.text = user.name ?: "Unknown"
            }

            photo.description?.let { description ->
                dividerLine1.visibility = View.VISIBLE
                descriptionText.visibility = View.VISIBLE
                descriptionText.text = description
            }

            statsContainer.visibility = View.VISIBLE
            txtLikes.text = "${photo.likes?.toAmountReadableString() ?: 0}"
            txtDownloads.text = "${photo.downloads?.toAmountReadableString() ?: 0}"
            txtViews.text = "${photo.views?.toAmountReadableString() ?: 0}"

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
            nestedScrollView.visibility = if (!isDisplayed) View.VISIBLE else View.GONE
        }
    }

    private fun displayErrorMsg(isDisplayed: Boolean) {
        binding.apply {
            layoutError.visibility = if (isDisplayed) View.VISIBLE else View.GONE
            nestedScrollView.visibility = if (!isDisplayed) View.VISIBLE else View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).apply {
            setTransparentStatusBar(isTransparent = true)
            toggleBottomNav(isVisible = false)
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).setTransparentStatusBar(isTransparent = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}