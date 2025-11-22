package ca.unb.mobiledev.nearmate.services

import ca.unb.mobiledev.nearmate.models.ChatMessage
import ca.unb.mobiledev.nearmate.models.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.util.concurrent.CompletableFuture

interface IChatService {
    fun sendMessage(receiverId: String, messageText: String): CompletableFuture<ChatMessage>
    fun listenForMessages(
        chatId: String,
        onNewMessage: (ChatMessage) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration
    fun listenForChatRooms(
        userId: String,
        onChatRoomsUpdated: (List<ChatRoom>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration
    fun getChatId(userId1: String, userId2: String): String
    fun chatExists(userId1: String, userId2: String): CompletableFuture<Boolean>
}

class ChatService : IChatService {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun getChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
    }

    override fun sendMessage(receiverId: String, messageText: String): CompletableFuture<ChatMessage> {
        val future = CompletableFuture<ChatMessage>()
        val senderId = firebaseAuth.currentUser?.uid ?: return future.apply {
            completeExceptionally(IllegalStateException("User not logged in"))
        }

        val chatId = getChatId(senderId, receiverId)
        val chatRef = firestore.collection("chats").document(chatId)
        val messageRef = chatRef.collection("messages").document()

        val timestamp = System.currentTimeMillis()
        val message = ChatMessage(
            id = messageRef.id,
            chatId = chatId,
            senderId = senderId,
            receiverId = receiverId,
            text = messageText,
            timestamp = timestamp
        )

        // Ensure participants are stored as strings
        val participants = listOf(senderId, receiverId)
        val chatData = mapOf(
            "participants" to participants,
            "lastMessage" to messageText,
            "lastMessageTimestamp" to timestamp
        )
        
        // Update or create chat document with last message info
        chatRef.set(chatData, SetOptions.merge())
            .addOnSuccessListener {
                // After chat document is updated, save the message
                messageRef.set(message.toMap())
                    .addOnSuccessListener { future.complete(message) }
                    .addOnFailureListener { e -> future.completeExceptionally(e) }
            }
            .addOnFailureListener { e -> future.completeExceptionally(e) }

        return future
    }

    override fun listenForMessages(
        chatId: String,
        onNewMessage: (ChatMessage) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    onError(e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    for (change in snapshots.documentChanges) {
                        if (change.type.name == "ADDED") {
                            val message = ChatMessage.fromMap(change.document.data)
                            onNewMessage(message)
                        }
                    }
                }
            }
    }

    override fun listenForChatRooms(
        userId: String,
        onChatRoomsUpdated: (List<ChatRoom>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore.collection("chats")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { chatSnapshots, e ->
                if (e != null) {
                    onError(e)
                    return@addSnapshotListener
                }

                if (chatSnapshots == null || chatSnapshots.isEmpty) {
                    onChatRoomsUpdated(emptyList())
                    return@addSnapshotListener
                }

                val chatRooms = mutableListOf<ChatRoom>()
                val pending = chatSnapshots.size()
                var processed = 0

                for (chatDoc in chatSnapshots.documents) {
                    val chatId = chatDoc.id
                    val participants = chatDoc.get("participants") as? List<*> ?: continue
                    // Convert all participants to strings and find the other user
                    val participantStrings = participants.mapNotNull { it?.toString() }
                    val otherUserId = participantStrings.firstOrNull { it != userId } ?: continue

                    // Read last message info directly from chat document
                    val lastMessage = chatDoc.getString("lastMessage") ?: ""
                    val lastMessageTimestamp = (chatDoc.get("lastMessageTimestamp") as? Number)?.toLong() ?: 0L

                    chatRooms.add(
                        ChatRoom(
                            chatId = chatId,
                            otherUserId = otherUserId as String,
                            lastMessage = lastMessage,
                            lastMessageTimestamp = lastMessageTimestamp
                        )
                    )
                }
                
                // Sort by timestamp and notify
                onChatRoomsUpdated(chatRooms.sortedByDescending { it.lastMessageTimestamp })
            }
    }

    override fun chatExists(userId1: String, userId2: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val chatId = getChatId(userId1, userId2)
        val chatRef = firestore.collection("chats").document(chatId)
        
        // Check if chat document exists and has messages
        chatRef.collection("messages")
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                future.complete(!snapshot.isEmpty)
            }
            .addOnFailureListener { e ->
                future.complete(false)
            }
        
        return future
    }
}
