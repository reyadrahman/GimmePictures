package ua.andrii.andrushchenko.gimmepictures.ui.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import ua.andrii.andrushchenko.gimmepictures.ui.adapters.PhotoExifAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.adapters.PhotoTagAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.viewmodels.PhotoDetailsViewModel
import ua.andrii.andrushchenko.gimmepictures.util.RecyclerViewSpacingItemDecoration
import ua.andrii.andrushchenko.gimmepictures.util.doOnApplyWindowInsets
import ua.andrii.andrushchenko.gimmepictures.util.setTransparentStatusBar
import ua.andrii.andrushchenko.gimmepictures.util.toAmountReadableString

@AndroidEntryPoint
class PhotoDetailsFragment : Fragment(R.layout.fragment_photo_details) {

    private val viewModel by viewModels<PhotoDetailsViewModel>()
    private val args by navArgs<PhotoDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPhotoDetailsBinding.bind(view)

        viewModel.getPhotoDetails(args.photoId)

        viewModel.photoDetails.observe(viewLifecycleOwner) { photo ->
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
                val appBarConfiguration = AppBarConfiguration(navController.graph)
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
                    //userContainer.setOnClickListener { listener.onUserClick(user) }
                    Glide.with(requireContext())
                        .load(user.profileImage?.medium)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_person)
                        .into(userImageView)
                    userTextView.text = user.name ?: "Unknown"
                }

                photo.description?.let { description ->
                    descriptionText.visibility = View.VISIBLE
                    descriptionText.text = description
                }

                txtLikes.text = "${photo.likes?.toAmountReadableString() ?: 0}"
                txtDownloads.text = "${photo.downloads?.toAmountReadableString() ?: 0}"
                txtViews.text = "${photo.views?.toAmountReadableString() ?: 0}"

                recyclerViewExif.adapter =
                    PhotoExifAdapter(requireContext()).apply { setExif(photo) }

                photo.tags?.let { tagList ->
                    recyclerViewTags.apply {
                        layoutManager = LinearLayoutManager(
                            context,
                            RecyclerView.HORIZONTAL,
                            false
                        ).apply {
                            addItemDecoration(
                                RecyclerViewSpacingItemDecoration(
                                    context,
                                    R.dimen.indent_12dp,
                                    RecyclerView.HORIZONTAL
                                )
                            )
                        }
                        adapter = PhotoTagAdapter(object : PhotoTagAdapter.OnTagClickListener {
                            override fun onTagClicked(tag: String) {
                                val direction = PhotoDetailsFragmentDirections
                                    .actionPhotoDetailsFragmentToSearchFragment(tag)
                                findNavController().navigate(direction)
                            }
                        }).apply {
                            submitList(tagList)
                        }
                    }
                }
            }
        }

        //setHasOptionsMenu(true)
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as AppCompatActivity).setTransparentStatusBar(isTransparent = true)
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).setTransparentStatusBar(isTransparent = false)
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_photo_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_share) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }*/
}