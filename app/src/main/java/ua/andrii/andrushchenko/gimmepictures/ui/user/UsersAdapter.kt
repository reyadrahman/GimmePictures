package ua.andrii.andrushchenko.gimmepictures.ui.user

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemUserBinding
import ua.andrii.andrushchenko.gimmepictures.domain.entities.User
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.util.loadImage

class UsersAdapter(private val listener: OnItemClickListener) :
    BasePagedAdapter<User>(USER_COMPARATOR) {

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
                        user?.let {
                            listener.onUserClick(user = it)
                        }
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun bind(entity: User) {
            with(binding) {
                userImageView.loadImage(
                    url = entity.profileImage?.medium,
                    placeholderColorDrawable = null
                )
                userNickNameTextView.text = "@${entity.username}"
                userNameTextView.text = "${entity.firstName} ${entity.lastName}"
            }
        }
    }

    interface OnItemClickListener {
        fun onUserClick(user: User)
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