package ru.magnum.messenger.data.remote.datasource

import kotlinx.coroutines.flow.Flow
import ru.magnum.messenger.data.remote.firebase.FirestoreMessageService
import ru.magnum.messenger.domain.model.Message
import javax.inject.Inject

class MessageRemoteDataSource @Inject constructor(
    private val service: FirestoreMessageService
) {
    suspend fun sendMessage(
        chatId: String,
        message: Message
    ) {
        service.sendMessage(chatId, message)
    }

    fun observeMessages(
        chatId: String
    ): Flow<List<Message>> {
        return service.observeMessages(
            chatId
        )
    }
}