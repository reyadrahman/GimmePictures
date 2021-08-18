package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemPhotoTagBinding
import ua.andrii.andrushchenko.gimmepictures.domain.Tag

class PhotoTagAdapter(
    private val onTagClickListener: (String) -> Unit
) : ListAdapter<Tag, PhotoTagAdapter.TagViewHolder>(TAG_COMPARATOR) {

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

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tag = getItem(position)
                    tag?.title?.let {
                        onTagClickListener(it)
                    }
                }
            }
        }

        fun bind(tag: Tag) {
            tag.title?.let {
                binding.chipPhotoTag.text = it
            }
        }
    }

    companion object {
        private val TAG_COMPARATOR = object : DiffUtil.ItemCallback<Tag>() {
            override fun areItemsTheSame(oldItem: Tag, newItem: Tag) =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Tag, newItem: Tag) =
                oldItem == newItem
        }
    }
}