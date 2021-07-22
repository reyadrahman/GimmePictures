package ua.andrii.andrushchenko.gimmepictures.ui.photo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemPhotoBinding
import ua.andrii.andrushchenko.gimmepictures.domain.entities.Photo
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.ui.widgets.AspectRatioImageView
import ua.andrii.andrushchenko.gimmepictures.util.loadImage
import ua.andrii.andrushchenko.gimmepictures.util.setAspectRatio

class PhotosAdapter(private val listener: OnItemClickListener) :
    BasePagedAdapter<Photo>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) : BaseViewHolder(binding) {

        init {
            with(binding) {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val photo = getItem(position)
                        photo?.let {
                            listener.onPhotoClick(photo = it, photoImageView)
                        }
                    }
                }
            }
        }

        override fun bind(entity: Photo) {
            with(binding) {
                ViewCompat.setTransitionName(photoCardView, "title_${entity.id}")
                photoImageView.apply {
                    setAspectRatio(entity.width, entity.height)
                    loadImage(
                        url = entity.urls.small,
                        placeholderColorDrawable = ColorDrawable(Color.parseColor(entity.color))
                    )
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onPhotoClick(photo: Photo, photoImageView: AspectRatioImageView)
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