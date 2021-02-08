package ua.andrii.andrushchenko.gimmepictures.ui.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemPhotoBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.util.setAspectRatio

class PhotosAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Photo, PhotosAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) holder.bind(currentItem)
    }

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.apply {
                photoImageView.setAspectRatio(photo.width, photo.height)
                photoImageView.setOnClickListener { listener.onPhotoClick(photo) }

                photo.user?.let { user ->
                    userContainer.isVisible = true
                    userContainer.setOnClickListener { listener.onUserClick(user) }
                    Glide.with(itemView)
                        .load(photo.user.profileImage?.medium)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_person)
                        .into(userImageView)
                    userTextView.text = user.name ?: "Unknown"
                    sponsoredTextView.isVisible = photo.sponsorship != null
                    sponsoredTextView.text = "Sponsored by ${photo.sponsorship?.sponsor?.name}"
                }

                Glide.with(itemView)
                    .load(photo.urls.regular)
                    .placeholder(ColorDrawable(Color.parseColor(photo.color)))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(ColorDrawable(Color.parseColor(photo.color)))
                    .into(photoImageView)

                userTextView.text = photo.user?.name
            }
        }
    }

    interface OnItemClickListener {
        fun onPhotoClick(photo: Photo)
        fun onUserClick(user: User)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                oldItem == newItem
        }
    }
}