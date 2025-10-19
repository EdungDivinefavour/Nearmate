package ca.unb.mobiledev.nearmate.features.home.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.models.ChatRoom
import java.text.DateFormat

class ChatListAdapter(
    private val chatRooms: List<ChatRoom>,
    private val onClick: (ChatRoom) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

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
        holder.txtName.text = room.otherUserId
        holder.txtLastMsg.text = room.lastMessage
        holder.txtTime.text = DateFormat.getTimeInstance().format(room.lastMessageTimestamp)
        holder.itemView.setOnClickListener { onClick(room) }
    }
}
