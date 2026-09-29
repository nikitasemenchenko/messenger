package ru.magnum.messenger.data.local.room.mapper

import ru.magnum.messenger.data.local.entity.MessageEntity
import ru.magnum.messenger.domain.model.Message

fun MessageEntity.toDomain(): Message {
    return Message(
        id = id,
        senderId = senderId,
        text = text,
        createdAt = createdAt
    )
}

fun Message.toEntity(
    chatId: String
): MessageEntity {
    return MessageEntity(
        id = id,
        senderId = senderId,
        text = text,
        createdAt = createdAt,
        chatId = chatId
    )
}