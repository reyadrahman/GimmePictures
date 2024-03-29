package ua.andrii.andrushchenko.gimmepictures.ui.collection.action_add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemCollectionSmallBinding
import ua.andrii.andrushchenko.gimmepictures.domain.Collection
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.util.loadImage

class AddToCollectionAdapter(
    private val onItemClickListener: (collection: Collection, selectedStateView: View, loadingProgress: View) -> Unit,
) : BasePagedAdapter<Collection>(COLLECTION_COMPARATOR) {

    lateinit var currentUserCollectionIds: List<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionSmallViewHolder {
        val binding =
            ItemCollectionSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionSmallViewHolder(binding)
    }

    inner class CollectionSmallViewHolder(private val binding: ItemCollectionSmallBinding) :
        BaseViewHolder(binding) {

        init {
            with(binding) {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val collection = getItem(position)
                        collection?.let {
                            onItemClickListener(
                                it,
                                selectedStateBackground,
                                layoutProgress
                            )
                        }
                    }
                }
            }
        }

        override fun bind(entity: Collection) = with(binding) {
            entity.coverPhoto?.let { photo ->
                collectionCoverPhotoImageView.loadImage(url = photo.urls.small)
            }
            txtCollectionName.text = entity.title
            collectionPrivateImageView.visibility =
                if (entity.private == true) View.VISIBLE else View.GONE
            val isPhotoInCollection = currentUserCollectionIds.contains(entity.id)
            selectedStateBackground.visibility =
                if (isPhotoInCollection) View.VISIBLE else View.GONE
        }
    }

    companion object {
        private val COLLECTION_COMPARATOR = object : DiffUtil.ItemCallback<Collection>() {
            override fun areItemsTheSame(oldItem: Collection, newItem: Collection) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Collection, newItem: Collection) =
                oldItem == newItem
        }
    }
}