package ua.andrii.andrushchenko.gimmepictures.ui.user.me

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentMyProfileBinding
import ua.andrii.andrushchenko.gimmepictures.ui.base.BaseFragment
import ua.andrii.andrushchenko.gimmepictures.util.loadImage

@AndroidEntryPoint
class MyProfileFragment :
    BaseFragment<FragmentMyProfileBinding>(FragmentMyProfileBinding::inflate) {
    private val viewModel: MyProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isAuthorized) {
            viewModel.obtainProfile()
            with(binding) {
                viewModel.me.observe(viewLifecycleOwner) { me ->
                    userImageView.loadImage(
                        url = me.profileImage?.large,
                        placeholderColorDrawable = null
                    )

                    @SuppressLint("SetTextI18n")
                    txtUsername.text = "${me.firstName} ${me.lastName}"
                }

                btnLogout.setOnClickListener {
                    viewModel.logout()
                }
            }
        } else {
            val direction = MyProfileFragmentDirections.actionNavMyProfileToAuthActivity()
            findNavController().navigate(direction)
        }
    }
}