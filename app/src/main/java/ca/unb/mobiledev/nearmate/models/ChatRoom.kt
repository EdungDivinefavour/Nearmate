package ca.unb.mobiledev.nearmate.models

data class ChatRoom(
    val chatId: String = "",
    val otherUserId: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L,
)

