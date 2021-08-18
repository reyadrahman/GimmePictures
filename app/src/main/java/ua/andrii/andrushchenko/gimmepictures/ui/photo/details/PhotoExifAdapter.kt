package ua.andrii.andrushchenko.gimmepictures.ui.photo.details

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.text.italic
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemPhotoExifBinding
import ua.andrii.andrushchenko.gimmepictures.domain.Photo
import java.util.*

class PhotoExifAdapter(
    val context: Context
) : ListAdapter<Triple<Int, Int, SpannableStringBuilder>, PhotoExifAdapter.ExifViewHolder>(
    diffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExifViewHolder {
        val binding =
            ItemPhotoExifBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExifViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExifViewHolder, position: Int) {
        val triple = getItem(position)
        holder.bind(triple.first, triple.second, triple.third)
    }

    fun setExif(photo: Photo) {
        val triples = mutableListOf<Triple<Int, Int, SpannableStringBuilder>>()
        val unknown = SpannableStringBuilder(context.getString(R.string.unknown))
        photo.exif?.let {
            val triplesArray = arrayOf(
                Triple(
                    R.string.camera,
                    R.drawable.ic_camera_outline,
                    if (it.model != null) SpannableStringBuilder().append(
                        formCameraName(
                            it.make,
                            it.model
                        )
                    )
                    else unknown
                ),
                Triple(
                    R.string.aperture,
                    R.drawable.ic_aperture,
                    if (it.aperture != null) SpannableStringBuilder().italic {
                        append("f")
                    }.append("/${it.aperture}") else unknown
                ),
                Triple(
                    R.string.focal_length,
                    R.drawable.ic_focal_length,
                    if (it.focalLength != null) SpannableStringBuilder(
                        "${it.focalLength}mm"
                    ) else unknown
                ),
                Triple(
                    R.string.shutter_speed,
                    R.drawable.ic_shutter_speed_outlined,
                    if (it.exposureTime != null) SpannableStringBuilder(
                        "${it.exposureTime}s"
                    ) else unknown
                ),
                Triple(
                    R.string.iso,
                    R.drawable.ic_iso_outlined,
                    if (it.iso != null) SpannableStringBuilder(it.iso.toString()) else unknown
                ),
                Triple(
                    R.string.resolution,
                    R.drawable.ic_resolution_outlined,
                    SpannableStringBuilder(
                        "${photo.width} × ${photo.height}"
                    )
                )
            )
            triples.addAll(triplesArray)
        }
        submitList(triples)
    }

    private fun formCameraName(make: String?, model: String): String {
        val makeList = make?.split(" ")?.map { it.trim() }
        val modelList = model.split(" ").map { it.trim() }
        return if (makeList?.map { it.lowercase(Locale.ROOT) }
                ?.intersect(modelList.map { it.lowercase(Locale.ROOT) })
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
            @StringRes
            stringRes: Int,
            @DrawableRes
            drawableRes: Int,
            value: SpannableStringBuilder
        ) {
            with(binding) {
                exifEntryTextView.setText(stringRes)
                exifEntryTextView.setCompoundDrawablesWithIntrinsicBounds(drawableRes, 0, 0, 0)
                exifValueTextView.text = value
            }
        }
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<Triple<Int, Int, SpannableStringBuilder>>() {
                override fun areItemsTheSame(
                    oldItem: Triple<Int, Int, SpannableStringBuilder>,
                    newItem: Triple<Int, Int, SpannableStringBuilder>,
                ) = oldItem.first == newItem.first

                override fun areContentsTheSame(
                    oldItem: Triple<Int, Int, SpannableStringBuilder>,
                    newItem: Triple<Int, Int, SpannableStringBuilder>,
                ) = oldItem == newItem
            }
    }
}