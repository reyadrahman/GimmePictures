package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.italic
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemPhotoExifBinding
import ua.andrii.andrushchenko.gimmepictures.models.Photo
import java.util.*

class PhotoExifAdapter(
    val context: Context
) : ListAdapter<Pair<Int, SpannableStringBuilder>, PhotoExifAdapter.ExifViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExifViewHolder {
        val binding =
            ItemPhotoExifBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExifViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExifViewHolder, position: Int) {
        val pair = getItem(position)
        holder.bind(pair.first, pair.second)
    }

    fun setExif(photo: Photo) {
        val pairs = mutableListOf<Pair<Int, SpannableStringBuilder>>()
        val unknown = SpannableStringBuilder(context.getString(R.string.unknown))
        photo.exif?.let {
            pairs.add(
                R.string.camera to if (it.model != null) SpannableStringBuilder().append(
                    formCameraName(it.make, it.model)
                ) else unknown
            )
            pairs.add(R.string.aperture to if (it.aperture != null) SpannableStringBuilder().italic {
                append(
                    "f"
                )
            }.append("/${it.aperture}") else unknown)
            pairs.add(R.string.focal_length to if (it.focalLength != null) SpannableStringBuilder("${it.focalLength}mm") else unknown)
            pairs.add(
                R.string.shutter_speed to if (it.exposureTime != null) SpannableStringBuilder(
                    "${it.exposureTime}s"
                ) else unknown
            )
            pairs.add(R.string.iso to if (it.iso != null) SpannableStringBuilder(it.iso.toString()) else unknown)
            pairs.add(
                R.string.resolution to SpannableStringBuilder(
                    "${photo.width} × ${photo.height}"
                )
            )
        }
        submitList(pairs)
    }

    private fun formCameraName(make: String?, model: String): String {
        val makeList = make?.split(" ")?.map { it.trim() }
        val modelList = model.split(" ").map { it.trim() }
        return if (makeList?.map { it.toLowerCase(Locale.ROOT) }
                ?.intersect(modelList.map { it.toLowerCase(Locale.ROOT) })
                ?.isEmpty() == true
        ) {
            "${makeList.first()} $model"
        } else {
            model
        }
    }

    inner class ExifViewHolder(private val binding: ItemPhotoExifBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            titleRes: Int,
            value: SpannableStringBuilder
        ) {
            binding.apply {
                exifTitleTextView.setText(titleRes)
                exifValueTextView.text = value
            }
        }
    }

    companion object {

        private val diffCallback =
            object : DiffUtil.ItemCallback<Pair<Int, SpannableStringBuilder>>() {
                override fun areItemsTheSame(
                    oldItem: Pair<Int, SpannableStringBuilder>,
                    newItem: Pair<Int, SpannableStringBuilder>
                ) = oldItem.first == newItem.first

                override fun areContentsTheSame(
                    oldItem: Pair<Int, SpannableStringBuilder>,
                    newItem: Pair<Int, SpannableStringBuilder>
                ) = oldItem == newItem
            }
    }
}