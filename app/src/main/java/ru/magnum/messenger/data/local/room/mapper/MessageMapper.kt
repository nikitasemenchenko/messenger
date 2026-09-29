package ru.magnum.messenger.data.local.room.mapper

import ru.magnum.messenger.data.local.entity.MessageEntity
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.model.MessageStatus

fun MessageEntity.toDomain(): Message {
    return Message(
        id = id,
        senderId = senderId,
        text = text,
        createdAt = createdAt,
        status = MessageStatus.valueOf(status)
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
        chatId = chatId,
        status = status.name
    )
}