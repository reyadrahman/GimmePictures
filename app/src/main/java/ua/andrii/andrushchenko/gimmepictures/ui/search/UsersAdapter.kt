package ua.andrii.andrushchenko.gimmepictures.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ua.andrii.andrushchenko.gimmepictures.databinding.ItemUserBinding
import ua.andrii.andrushchenko.gimmepictures.models.User
import ua.andrii.andrushchenko.gimmepictures.ui.base.BasePagedAdapter
import ua.andrii.andrushchenko.gimmepictures.util.loadImage

class UsersAdapter(private val listener: OnItemClickListener) :
    BasePagedAdapter<User>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) : BaseViewHolder(binding) {
        @SuppressLint("SetTextI18n")
        override fun bind(entity: User) {
            with(binding) {
                userImageView.loadImage(
                    url = entity.profileImage?.medium,
                    placeholderColorDrawable = null
                )
                userNickNameTextView.text = "@${entity.username}"
                userNameTextView.text = "${entity.firstName} ${entity.lastName}"
                userCard.setOnClickListener { listener.onUserClick(entity) }
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