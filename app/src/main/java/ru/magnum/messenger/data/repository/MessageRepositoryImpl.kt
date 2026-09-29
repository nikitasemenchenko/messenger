package ru.magnum.messenger.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.magnum.messenger.data.local.datasource.MessageLocalDataSource
import ru.magnum.messenger.data.local.room.mapper.toDomain
import ru.magnum.messenger.data.local.room.mapper.toEntity
import ru.magnum.messenger.data.remote.datasource.MessageRemoteDataSource
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.model.MessageStatus
import ru.magnum.messenger.domain.repository.MessageRepository
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val local: MessageLocalDataSource,
    private val remote: MessageRemoteDataSource
) : MessageRepository {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    override fun getMessages(chatId: String): Flow<List<Message>> {
        return local.getMessages(chatId).map { entities ->
            entities.map {
                it.toDomain()
            }
        }
    }

    override suspend fun sendMessage(
        chatId: String,
        message: Message
    ): Result<Unit> {
        val sendingMessage = message.copy(
            status = MessageStatus.SENDING
        )

        return try {
            local.insertMessage(
                sendingMessage.toEntity(chatId)
            )

            remote.sendMessage(
                chatId,
                sendingMessage
            )
            local.updateStatus(
                message.id,
                MessageStatus.SENT.name
            )
            Result.success(Unit)
        } catch (e: Exception) {
            local.updateStatus(
                message.id,
                MessageStatus.FAILED.name
            )
            Result.failure(e)
        }
    }

    override fun syncMessages(
        chatId: String
    ) {
        remote.observeMessages(chatId)
            .onEach { messages ->
                local.insertMessages(
                    messages.map {
                        it.toEntity(chatId)
                    }
                )
            }
            .launchIn(scope)
    }
}