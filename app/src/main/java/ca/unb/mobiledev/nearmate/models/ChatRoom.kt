package ca.unb.mobiledev.nearmate.models

data class ChatRoom(
    val chatId: String = "",
    val otherUserId: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "chatId" to chatId,
            "lastMessage" to lastMessage,
            "lastMessageTimestamp" to lastMessageTimestamp
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>, otherUserId: String): ChatRoom {
            return ChatRoom(
                chatId = map["chatId"] as? String ?: "",
                otherUserId = otherUserId,
                lastMessage = map["lastMessage"] as? String ?: "",
                lastMessageTimestamp = (map["lastMessageTimestamp"] as? Number)?.toLong() ?: 0L
            )
        }
    }
}

