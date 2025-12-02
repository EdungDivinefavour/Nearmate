package ca.unb.mobiledev.nearmate.features.home.users

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.features.userdetails.UserDetailsActivity
import ca.unb.mobiledev.nearmate.models.User
import com.bumptech.glide.Glide

class UserListAdapter(
    private var users: List<User>
) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UserDetailsActivity::class.java)
            intent.putExtra("user", users[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTv: TextView = view.findViewById(R.id.userNameTv)
        private val statusTv: TextView = view.findViewById(R.id.statusTv)
        private val profilePic: ImageView = view.findViewById(R.id.profilePicture)
        private val countryFlag: ImageView = view.findViewById(R.id.countryFlag)

        fun bind(user: User) {
            val context = itemView.context

            nameTv.text = if (user.prefersToShowUserName && !user.userName.isNullOrEmpty()) {
                user.userName
            } else {
                "${user.firstName} ${user.lastName}"
            }

            statusTv.text = user.status.value

            if (!user.profilePhoto.isNullOrEmpty()) {
                Glide.with(context)
                    .load(user.profilePhoto)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .circleCrop()
                    .into(profilePic)
            } else {
                profilePic.setImageResource(R.drawable.baseline_account_circle_24)
            }

            // Only bothering about these 4 most popular countries to save us time
            val flagResId = when (user.country?.value?.lowercase()) {
                "nigeria" -> R.drawable.nigeria
                "canada" -> R.drawable.canada
                "ghana" -> R.drawable.ghana
                "india" -> R.drawable.india
                else -> R.drawable.nigeria
            }
            countryFlag.setImageResource(flagResId)
        }
    }
}
