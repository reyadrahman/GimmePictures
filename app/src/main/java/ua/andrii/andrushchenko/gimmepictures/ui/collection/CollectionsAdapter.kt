package ua.andrii.andrushchenko.gimmepictures.ui.collection

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemCollectionBinding
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.util.loadImage

class CollectionsAdapter(
    private val listener: (collection: Collection) -> Unit
) : BasePagedAdapter<Collection>(COLLECTION_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(binding)
    }

    inner class CollectionViewHolder(private val binding: ItemCollectionBinding) :
        BaseViewHolder(binding) {

        init {
            with(binding) {
                collectionCoverPhotoImageView.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val collection = getItem(position)
                        collection?.let {
                            listener(it)
                        }
                    }
                }
            }
        }

        override fun bind(entity: Collection) {
            with(binding) {
                collectionCoverPhotoImageView.apply {
                    entity.coverPhoto?.let { coverPhoto ->
                        loadImage(
                            url = entity.coverPhoto.urls.small,
                            placeholderColorDrawable = ColorDrawable(Color.parseColor(coverPhoto.color))
                        )
                    }
                }
                collectionName.text = entity.title
            }
        }
    }

    companion object {
        private val COLLECTION_COMPARATOR = object : DiffUtil.ItemCallback<Collection>() {
            override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean =
                oldItem == newItem
        }
    }
}