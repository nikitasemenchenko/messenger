package ru.magnum.messenger.domain.model

data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val createdAt: Long,
    val status: MessageStatus = MessageStatus.SENDING
)