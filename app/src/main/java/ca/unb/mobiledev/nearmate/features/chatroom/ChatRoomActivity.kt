package ca.unb.mobiledev.nearmate.features.chatroom

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.models.ChatMessage
import ca.unb.mobiledev.nearmate.services.ChatService
import com.google.firebase.auth.FirebaseAuth

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: MessageAdapter

    private val chatService = ChatService()
    private val messages = mutableListOf<ChatMessage>()
    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    private lateinit var chatId: String
    private lateinit var otherUserId: String
    private val senderId by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        chatId = intent.getStringExtra("chatId") ?: ""
        otherUserId = intent.getStringExtra("otherUserId") ?: ""

        recyclerView = findViewById(R.id.recyclerViewMessages)
        editText = findViewById(R.id.editMessage)
        sendButton = findViewById(R.id.btnSend)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(messages, senderId)
        recyclerView.adapter = adapter

        listenForMessages()

        sendButton.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isNotEmpty()) {
                chatService.sendMessage(otherUserId, text)
                editText.text.clear()
            }
        }
    }

    private fun listenForMessages() {
        listenerRegistration = chatService.listenForMessages(
            chatId = chatId,
            onNewMessage = { message ->
                messages.add(message)
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
            },
            onError = { e -> e.printStackTrace() }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}
