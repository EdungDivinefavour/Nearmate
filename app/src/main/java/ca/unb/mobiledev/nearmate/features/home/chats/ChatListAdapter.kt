package ca.unb.mobiledev.nearmate.features.home.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.models.ChatRoom
import ca.unb.mobiledev.nearmate.models.User
import ca.unb.mobiledev.nearmate.services.UserService
import java.text.DateFormat

class ChatListAdapter(
    private val chatRooms: List<ChatRoom>,
    private val onClick: (ChatRoom) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    private val userService = UserService()
    private val userCache = mutableMapOf<String, String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtUserName)
        val txtLastMsg: TextView = view.findViewById(R.id.txtLastMessage)
        val txtTime: TextView = view.findViewById(R.id.txtTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_room, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = chatRooms.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = chatRooms[position]
        
        // Display last message and time
        holder.txtLastMsg.text = if (room.lastMessage.isNotEmpty()) {
            room.lastMessage
        } else {
            "No messages yet"
        }
        holder.txtTime.text = if (room.lastMessageTimestamp > 0) {
            DateFormat.getTimeInstance().format(java.util.Date(room.lastMessageTimestamp))
        } else {
            ""
        }
        
        // Load user name
        if (userCache.containsKey(room.otherUserId)) {
            holder.txtName.text = userCache[room.otherUserId]
        } else {
            holder.txtName.text = room.otherUserId // Show ID temporarily
            userService.getUserById(room.otherUserId).thenAccept { user ->
                if (user != null) {
                    val displayName = if (user.prefersToShowUserName && !user.userName.isNullOrEmpty()) {
                        user.userName
                    } else {
                        "${user.firstName} ${user.lastName}"
                    }
                    userCache[room.otherUserId] = displayName
                    holder.itemView.post {
                        holder.txtName.text = displayName
                    }
                }
            }
        }
        
        holder.itemView.setOnClickListener { onClick(room) }
    }
}
