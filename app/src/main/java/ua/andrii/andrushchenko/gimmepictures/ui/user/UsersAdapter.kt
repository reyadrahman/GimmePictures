package ua.andrii.andrushchenko.gimmepictures.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.R
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemUserBinding
import ua.andrii.andrushchenko.gimmepictures.domain.User
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.util.loadImage

class UsersAdapter(
    private val listener: (user: User) -> Unit
) : BasePagedAdapter<User>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : BaseViewHolder(binding) {

        init {
            with(binding) {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val user = getItem(position)
                        user?.let { listener(it) }
                    }
                }
            }
        }

        override fun bind(entity: User) {
            with(binding) {
                userImageView.loadImage(url = entity.profileImage?.medium)
                userNickNameTextView.text = root.resources.getString(
                    R.string.user_nickname_formatted,
                    entity.username
                )
                userNameTextView.text = root.resources.getString(
                    R.string.user_full_name_formatted,
                    entity.firstName,
                    entity.lastName
                )
            }
        }
    }

    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
        }
    }
}