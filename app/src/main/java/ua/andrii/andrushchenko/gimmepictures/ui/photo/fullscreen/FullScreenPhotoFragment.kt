package ua.andrii.andrushchenko.gimmepictures.ui.photo.fullscreen

import androidx.fragment.app.Fragment
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.ui.activities.MainActivity

class FullScreenPhotoFragment : Fragment(R.layout.fragment_fullscreen_photo) {

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).toggleBottomNav(isVisible = false)
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).toggleBottomNav(isVisible = true)
    }

}