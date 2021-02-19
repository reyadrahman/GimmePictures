package ua.andrii.andrushchenko.gimmepictures.ui.collection.photos

import androidx.fragment.app.Fragment
import ua.andrii.andrushchenko.gimmepictures.ui.activities.MainActivity

class CollectionPhotosFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).toggleBottomNav(isVisible = false)
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).toggleBottomNav(isVisible = true)
    }

}