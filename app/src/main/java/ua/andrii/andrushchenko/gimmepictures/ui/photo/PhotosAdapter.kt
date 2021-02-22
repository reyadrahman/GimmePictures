package ua.andrii.andrushchenko.gimmepictures.ui.photo

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemPhotoBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.util.setAspectRatio

class PhotosAdapter(private val listener: OnItemClickListener) :
    BasePagedAdapter<Photo>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewPhotoViewHolder(binding)
    }

    inner class NewPhotoViewHolder(private val binding: ItemPhotoBinding) :
        BaseViewHolder(binding) {

        @SuppressLint("SetTextI18n")
        override fun bind(entity: Photo) {
            binding.apply {
                photoImageView.setAspectRatio(entity.width, entity.height)
                photoImageView.setOnClickListener { listener.onPhotoClick(entity) }

                entity.user?.let { user ->
                    userContainer.isVisible = true
                    userContainer.setOnClickListener { listener.onUserClick(user) }
                    Glide.with(itemView.context)
                        .load(entity.user.profileImage?.small)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_person)
                        .into(userImageView)
                    userTextView.text = user.name ?: "Unknown"
                    sponsoredTextView.isVisible = entity.sponsorship != null
                    sponsoredTextView.text =
                        "${itemView.context.getString(R.string.sponsored_by)} ${entity.sponsorship?.sponsor?.name}"
                }

                Glide.with(itemView.context)
                    .load(entity.urls.regular)
                    .placeholder(ColorDrawable(Color.parseColor(entity.color)))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(ColorDrawable(Color.parseColor(entity.color)))
                    .into(photoImageView)
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