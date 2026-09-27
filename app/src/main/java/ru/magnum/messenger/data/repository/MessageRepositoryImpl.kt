package ru.magnum.messenger.data.repository

import kotlinx.coroutines.flow.Flow
import ru.magnum.messenger.data.remote.firebase.FirestoreMessageService
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.repository.MessageRepository
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val service: FirestoreMessageService
): MessageRepository {
    override fun getMessages(chatId: String): Flow<List<Message>> {
        return service.getMessages(chatId)
    }

    override suspend fun sendMessage(
        chatId: String,
        message: Message
    ): Result<Unit> {
        return try {
            service.sendMessage(chatId, message)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}