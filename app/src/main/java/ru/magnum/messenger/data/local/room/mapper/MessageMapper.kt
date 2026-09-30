package ru.magnum.messenger.data.local.room.mapper

import ru.magnum.messenger.data.local.entity.MessageEntity
import ru.magnum.messenger.data.local.entity.PendingMessageEntity
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.model.MessageStatus

fun MessageEntity.toDomain(): Message {
    return Message(
        id = id,
        senderId = senderId,
        text = text,
        createdAt = createdAt,
        status = MessageStatus.entries.firstOrNull { it.name == status } ?: MessageStatus.SENT
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

fun Message.toPendingEntity(
    chatId: String
): PendingMessageEntity {
    return PendingMessageEntity(
        id = id,
        chatId = chatId,
        senderId = senderId,
        text = text,
        createdAt = createdAt
    )
}

fun PendingMessageEntity.toDomain(): Message {
    return Message(
        id = id,
        senderId = senderId,
        text = text,
        createdAt = createdAt,
        status = MessageStatus.SENDING
    )
}
