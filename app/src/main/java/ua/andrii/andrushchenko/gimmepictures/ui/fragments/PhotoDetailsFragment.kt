package ua.andrii.andrushchenko.gimmepictures.ui.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentPhotoDetailsBinding

class PhotoDetailsFragment : Fragment(R.layout.fragment_photo_details) {

    private val args by navArgs<PhotoDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPhotoDetailsBinding.bind(view)

        val photo = args.photo

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "City, Country"

        binding.apply {
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

            Glide.with(requireContext())
                .load(photo.urls.regular)
                .placeholder(ColorDrawable(Color.parseColor(photo.color)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(ColorDrawable(Color.parseColor(photo.color)))
                .into(photoImageView)

            photo.description?.let { description ->
                descriptionText.visibility = View.VISIBLE
                descriptionText.text = description
            }
        }
    }

}