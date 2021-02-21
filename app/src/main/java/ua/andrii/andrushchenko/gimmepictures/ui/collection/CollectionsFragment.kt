package ua.andrii.andrushchenko.gimmepictures.ui.collection

import androidx.fragment.app.Fragment
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.ui.activities.MainActivity

class CollectionsFragment : Fragment(R.layout.fragment_collections) {

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).toggleBottomNav(isVisible = true)
    }

}