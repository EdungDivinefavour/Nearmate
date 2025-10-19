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

        val message = ChatMessage(
            id = messageRef.id,
            chatId = chatId,
            senderId = senderId,
            receiverId = receiverId,
            text = messageText,
            timestamp = System.currentTimeMillis()
        )

        val chatData = mapOf("participants" to listOf(senderId, receiverId))
        chatRef.set(chatData, SetOptions.merge())

        messageRef.set(message.toMap())
            .addOnSuccessListener { future.complete(message) }
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
                    val otherUserId = participants.firstOrNull { it != userId } ?: continue

                    firestore.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { msgSnapshot ->
                            val lastDoc = msgSnapshot.documents.firstOrNull()
                            val lastMessage = lastDoc?.getString("text") ?: ""
                            val timestamp = (lastDoc?.get("timestamp") as? Number)?.toLong() ?: 0L

                            chatRooms.add(
                                ChatRoom(
                                    chatId = chatId,
                                    otherUserId = otherUserId as String,
                                    lastMessage = lastMessage,
                                    lastMessageTimestamp = timestamp
                                )
                            )

                            processed++
                            if (processed == pending) {
                                onChatRoomsUpdated(chatRooms.sortedByDescending { it.lastMessageTimestamp })
                            }
                        }
                        .addOnFailureListener {
                            processed++
                            if (processed == pending) {
                                onChatRoomsUpdated(chatRooms)
                            }
                        }
                }
            }
    }
}
