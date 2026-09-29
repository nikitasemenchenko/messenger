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

    fun syncMessages(
        chatId: String
    )
}