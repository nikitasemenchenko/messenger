package ru.magnum.messenger.data.repository

import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.magnum.messenger.data.local.room.dao.MessageDao
import ru.magnum.messenger.data.local.room.mapper.toDomain
import ru.magnum.messenger.data.local.room.mapper.toEntity
import ru.magnum.messenger.data.remote.firebase.FirestoreMessageService
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.model.MessageStatus
import ru.magnum.messenger.domain.repository.MessageRepository
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val service: FirestoreMessageService,
    private val messageDao: MessageDao
): MessageRepository {
    private var listeners = mutableMapOf<String, ListenerRegistration>()

    override fun getMessages(chatId: String): Flow<List<Message>> {
        return messageDao.getMessages(chatId).map { entities ->
            entities.map {
                it.toDomain()
            }
        }
    }

    override suspend fun sendMessage(
        chatId: String,
        message: Message
    ): Result<Unit> {
        val sendingMessage =  message.copy(
            status = MessageStatus.SENDING
        )

        messageDao.insertMessage(
            sendingMessage.toEntity(chatId)
        )

        return try {
            service.sendMessage(
                chatId,
                sendingMessage
            )
            messageDao.updateStatus(
                messageId = message.id,
                status = MessageStatus.SENT.name
            )
            Result.success(Unit)
        } catch (e: Exception){
            messageDao.updateStatus(
                messageId = message.id,
                status = MessageStatus.FAILED.name
            )
            Result.failure(e)
        }
    }

    override fun syncMessages(chatId: String) {
        if(listeners.containsKey(chatId)){
            return
        }

        val listener = service.observeMessages(
            chatId
        ) { messages ->
            CoroutineScope(
                Dispatchers.IO
            ).launch {
                messageDao.insertMessages(
                    messages.map {
                        it.toEntity(chatId)
                    }
                )
            }
        }
        listeners[chatId] = listener
    }

}