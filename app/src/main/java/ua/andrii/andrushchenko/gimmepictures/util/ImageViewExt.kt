package ua.andrii.andrushchenko.gimmepictures.util

import ua.andrii.andrushchenko.gimmepictures.ui.widgets.AspectRatioImageView

fun AspectRatioImageView.setAspectRatio(width: Int?, height: Int?) {
    if (width != null && height != null) {
        aspectRatio = height.toDouble() / width.toDouble()
    }
}