package ua.andrii.andrushchenko.gimmepictures.ui.collection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ua.andrii.andrushchenko.gimmepictures.GlideApp
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemCollectionBinding
import ua.andrii.andrushchenko.gimmepictures.models.Collection
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter

class CollectionsAdapter(private val listener: OnItemClickListener) :
    BasePagedAdapter<Collection>(COLLECTION_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(binding)
    }

    inner class CollectionViewHolder(private val binding: ItemCollectionBinding) :
        BaseViewHolder(binding) {

        override fun bind(entity: Collection) {
            with(binding) {
                collectionCoverPhotoImageView.setOnClickListener {
                    listener.onCollectionClick(entity)
                }

                GlideApp.with(root.context)
                    .load(entity.coverPhoto?.urls?.small)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    /*.apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))*/
                    .into(collectionCoverPhotoImageView)

                collectionName.text = entity.title
            }
        }
    }

    interface OnItemClickListener {
        fun onCollectionClick(collection: Collection)
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