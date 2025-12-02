package ca.unb.mobiledev.nearmate.features.home.chats

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.features.chatroom.ChatRoomActivity
import ca.unb.mobiledev.nearmate.models.ChatRoom
import ca.unb.mobiledev.nearmate.services.ChatService
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class ChatListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var adapter: ChatListAdapter
    private val chatService = ChatService()
    private val chatRooms = mutableListOf<ChatRoom>()
    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_chat_list)
        emptyStateText = view.findViewById(R.id.empty_state_text)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ChatListAdapter(chatRooms) { chatRoom ->
            val intent = Intent(requireContext(), ChatRoomActivity::class.java)
            intent.putExtra("chatId", chatRoom.chatId)
            intent.putExtra("otherUserId", chatRoom.otherUserId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        listenForChatRooms()
        return view
    }

    private fun listenForChatRooms() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        listenerRegistration = chatService.listenForChatRooms(
            userId = currentUserId,
            onChatRoomsUpdated = { updatedList ->
                chatRooms.clear()
                chatRooms.addAll(updatedList)
                adapter.notifyDataSetChanged()

                if (updatedList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyStateText.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyStateText.visibility = View.GONE
                }
            },
            onError = { e -> e.printStackTrace() }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
    }
}
