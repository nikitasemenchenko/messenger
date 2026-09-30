package ru.magnum.messenger.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.magnum.messenger.domain.model.Message

interface MessageRepository {
    fun getMessages(
        chatId: String
    ): Flow<List<Message>>

    suspend fun sendMessage(
        chatId: String,
        message: Message
    ): Result<Unit>

    // Повторная отправка сообщения со статусом FAILED
    suspend fun retryMessage(
        messageId: String
    ): Result<Unit>

    // Слушает сервер и складывает сообщения в локальную БД.
    suspend fun syncMessages(
        chatId: String
    )

}