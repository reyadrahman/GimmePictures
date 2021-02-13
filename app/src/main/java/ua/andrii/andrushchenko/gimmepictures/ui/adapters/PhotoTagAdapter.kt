package ua.andrii.andrushchenko.gimmepictures.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemPhotoTagBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo

class PhotoTagAdapter(private val onTagClickListener: OnTagClickListener) :
    ListAdapter<Photo.Tag, PhotoTagAdapter.TagViewHolder>(TAG_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemPhotoTagBinding.inflate(LayoutInflater.from(parent.context))
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) holder.bind(currentItem)
    }

    inner class TagViewHolder(private val binding: ItemPhotoTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: Photo.Tag) {
            tag.title?.let { title ->
                binding.apply {
                    chipPhotoTag.text = title
                    chipPhotoTag.setOnClickListener {
                        onTagClickListener.onTagClicked(title)
                    }
                }
            }
        }
    }

    interface OnTagClickListener {
        fun onTagClicked(tag: String)
    }

    companion object {
        private val TAG_COMPARATOR = object : DiffUtil.ItemCallback<Photo.Tag>() {
            override fun areItemsTheSame(oldItem: Photo.Tag, newItem: Photo.Tag) =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Photo.Tag, newItem: Photo.Tag) =
                oldItem == newItem
        }
    }
}