package ru.magnum.messenger.data.local.datasource

import kotlinx.coroutines.flow.Flow
import ru.magnum.messenger.data.local.entity.MessageEntity
import ru.magnum.messenger.data.local.room.dao.MessageDao
import javax.inject.Inject

class MessageLocalDataSource @Inject constructor(
    private val messageDao: MessageDao
) {
    fun getMessages(
        chatId: String
    ): Flow<List<MessageEntity>> {
        return messageDao.getMessages(chatId)
    }

    suspend fun insertMessage(
        message: MessageEntity
    ) {
        messageDao.insertMessage(
            message
        )
    }

    suspend fun insertMessages(
        messages: List<MessageEntity>
    ) {
        messageDao.insertMessages(
            messages
        )
    }

    suspend fun updateStatus(
        id: String,
        status: String
    ) {
        messageDao.updateStatus(
            id,
            status
        )
    }
}