package ru.magnum.messenger.data.local.datasource

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import ru.magnum.messenger.data.local.entity.MessageEntity
import ru.magnum.messenger.data.local.entity.PendingMessageEntity
import ru.magnum.messenger.data.local.room.AppDatabase
import ru.magnum.messenger.data.local.room.dao.MessageDao
import ru.magnum.messenger.data.local.room.dao.PendingMessageDao
import ru.magnum.messenger.domain.model.MessageStatus
import javax.inject.Inject

class MessageLocalDataSource @Inject constructor(
    private val db: AppDatabase,
    private val messageDao: MessageDao,
    private val pendingDao: PendingMessageDao
) {
    fun getMessages(chatId: String): Flow<List<MessageEntity>> =
        messageDao.getMessages(chatId)

    // Сообщения пришедшие с сервера (они уже точно доставлены)
    suspend fun insertMessages(messages: List<MessageEntity>) {
        messageDao.insertMessages(messages)
    }

    // шаг 1 отправки. Сообщение и запись в очереди пишутся в одной транзакции
    suspend fun enqueueMessages(
        message: MessageEntity,
        pending: PendingMessageEntity
    ) {
        db.withTransaction {
            messageDao.insertMessage(message)
            pendingDao.insert(pending)
        }
    }

    suspend fun getPendingMessages(): List<PendingMessageEntity> =
        pendingDao.getPendingMessages()

    suspend fun pendingCount(): Int = pendingDao.count()

    // Сервер подтвердил запись - ставим статус SENT + удаляем из очереди атомарно
    suspend fun markSent(id: String) {
        db.withTransaction {
            messageDao.updateStatus(id, MessageStatus.SENT.name)
            pendingDao.deleteById(id)
        }
    }

    // Сервер отклонил сообщение окончательно - статус FAILED и из очереди убираем
    suspend fun markFailed(id: String) {
        db.withTransaction {
            messageDao.updateStatus(id, MessageStatus.FAILED.name)
            pendingDao.deleteById(id)
        }
    }

    // Повторная отправка FAILED сообщения
    // Возвращает true если реально вернули в очередь
    suspend fun requeueFailed(id: String): Boolean = db.withTransaction {
        val message = messageDao.getById(id) ?: return@withTransaction false
        if (message.status != MessageStatus.FAILED.name) return@withTransaction false

        messageDao.updateStatus(id, MessageStatus.SENDING.name)
        pendingDao.insert(
            PendingMessageEntity(
                id = message.id,
                chatId = message.chatId,
                senderId = message.senderId,
                text = message.text,
                createdAt = message.createdAt
            )
        )
        true
    }
}
