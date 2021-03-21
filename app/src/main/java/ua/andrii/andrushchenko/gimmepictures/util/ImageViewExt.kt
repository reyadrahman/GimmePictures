package ua.andrii.andrushchenko.gimmepictures.util

import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.SupportRSBlurTransformation
import ua.andrii.andrushchenko.gimmepictures.GlideApp
import ua.andrii.andrushchenko.gimmepictures.ui.widgets.AspectRatioImageView

fun AspectRatioImageView.setAspectRatio(width: Int?, height: Int?) {
    if (width != null && height != null) {
        aspectRatio = height.toDouble() / width.toDouble()
    }
}

fun ImageView.loadImage(url: String?, placeholderColorDrawable: ColorDrawable?) {
    GlideApp.with(this)
        .load(url)
        .placeholder(placeholderColorDrawable)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
        .clearOnDetach()
}

fun ImageView.loadBlurredImage(url: String?, placeholderColorDrawable: ColorDrawable?) {
    GlideApp.with(this)
        .load(url)
        .placeholder(placeholderColorDrawable)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(RequestOptions.bitmapTransform(SupportRSBlurTransformation()))
        .into(this)
        .clearOnDetach()
}