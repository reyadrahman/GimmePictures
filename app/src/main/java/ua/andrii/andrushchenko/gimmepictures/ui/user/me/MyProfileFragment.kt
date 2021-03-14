package ua.andrii.andrushchenko.gimmepictures.ui.user.me

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.FragmentMyProfileBinding

@AndroidEntryPoint
class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isAuthorized) {
            viewModel.obtainProfile()
            with(binding) {
                viewModel.me.observe(viewLifecycleOwner) { me ->
                    Glide.with(requireContext())
                        .load(me.profileImage?.large)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_person)
                        .into(userImageView)
                        .clearOnDetach()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}