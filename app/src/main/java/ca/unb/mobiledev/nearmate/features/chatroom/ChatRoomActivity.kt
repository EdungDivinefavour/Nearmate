package ca.unb.mobiledev.nearmate.features.chatroom

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.unb.mobiledev.nearmate.R
import ca.unb.mobiledev.nearmate.models.ChatMessage
import ca.unb.mobiledev.nearmate.models.User
import ca.unb.mobiledev.nearmate.services.ChatService
import ca.unb.mobiledev.nearmate.services.UserService
import com.google.firebase.auth.FirebaseAuth

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: MessageAdapter

    private val chatService = ChatService()
    private val userService = UserService()
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

        // Set up action bar with back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.WHITE))
        supportActionBar?.title = "" // Clear default title

        // Get user object if passed, otherwise fetch it
        val otherUser = intent.getParcelableExtra<User>("otherUser")
        if (otherUser != null) {
            // User object was passed, use it directly
            val displayName = if (otherUser.prefersToShowUserName && !otherUser.userName.isNullOrEmpty()) {
                otherUser.userName
            } else {
                "${otherUser.firstName} ${otherUser.lastName}"
            }
            supportActionBar?.title = displayName
        } else {
            // User object not passed (e.g., from chat list), fetch it
            userService.getUserById(otherUserId).thenAccept { user ->
                if (user != null) {
                    val displayName = if (user.prefersToShowUserName && !user.userName.isNullOrEmpty()) {
                        user.userName
                    } else {
                        "${user.firstName} ${user.lastName}"
                    }
                    runOnUiThread {
                        supportActionBar?.title = displayName
                    }
                }
            }
        }

        recyclerView = findViewById(R.id.recyclerViewMessages)
        editText = findViewById(R.id.editMessage)
        sendButton = findViewById(R.id.btnSend)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(messages, senderId)
        recyclerView.adapter = adapter

        // Update send button color based on text field content
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasText = s?.toString()?.trim()?.isNotEmpty() == true
                val color = if (hasText) {
                    ContextCompat.getColor(this@ChatRoomActivity, R.color.colorPrimary)
                } else {
                    ContextCompat.getColor(this@ChatRoomActivity, android.R.color.darker_gray)
                }
                sendButton.setColorFilter(color)
            }
        })
        // Set initial color
        sendButton.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))

        listenForMessages()

        sendButton.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isNotEmpty()) {
                chatService.sendMessage(otherUserId, text)
                editText.text.clear()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
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
