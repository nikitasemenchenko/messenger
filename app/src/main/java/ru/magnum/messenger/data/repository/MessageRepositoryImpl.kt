package ru.magnum.messenger.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.magnum.messenger.core.di.ApplicationScope
import ru.magnum.messenger.data.local.datasource.MessageLocalDataSource
import ru.magnum.messenger.data.local.room.mapper.toDomain
import ru.magnum.messenger.data.local.room.mapper.toEntity
import ru.magnum.messenger.data.local.room.mapper.toPendingEntity
import ru.magnum.messenger.data.network.NetworkMonitor
import ru.magnum.messenger.data.remote.datasource.MessageRemoteDataSource
import ru.magnum.messenger.data.remote.firebase.isTempError
import ru.magnum.messenger.data.sync.MessageSyncScheduler
import ru.magnum.messenger.data.sync.PendingMessageSender
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.model.MessageStatus
import ru.magnum.messenger.domain.repository.MessageRepository
import javax.inject.Inject
import kotlin.comparisons.minOf
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

class MessageRepositoryImpl @Inject constructor(
    private val local: MessageLocalDataSource,
    private val remote: MessageRemoteDataSource,
    private val pendingMessageSender: PendingMessageSender,
    private val scheduler: MessageSyncScheduler,
    private val networkMonitor: NetworkMonitor,
    @ApplicationScope private val appScope: CoroutineScope
) : MessageRepository {

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
        val message = message.copy(status = MessageStatus.SENDING)

        return try {
            // NonCancellable - если пользователь ушёл с экрана в этот момент
            // запись не должна откатиться - иначе сообщение пропадёт
            withContext(NonCancellable) {
                local.enqueueMessages(
                    message = message.toEntity(chatId),
                    pending = message.toPendingEntity(chatId)
                )
                sendPendingMessages()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save message", e)
            Result.failure(e)
        }
    }

    override suspend fun retryMessage(messageId: String): Result<Unit> {
        return try {
            withContext(NonCancellable) {
                if (local.requeueFailed(messageId)) {
                    sendPendingMessages()
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to requeue message $messageId", e)
            Result.failure(e)
        }
    }

    // никаких вечных слушателей, которые копятся при каждом открытии чата
    override suspend fun syncMessages(chatId: String) {
        remote.observeMessages(chatId)
            .retryWhen { cause, attempt ->
                if (!cause.isTempError()) return@retryWhen false
                delay(minOf(MAX_SYNC_RETRY_DELAY_MS, 1_000L shl minOf(attempt, 5L).toInt()).milliseconds)
                true
            }
            .onEach { messages ->
                local.insertMessages(messages.map { it.toEntity(chatId) })
            }
            .catch { e ->
                // Не даём ошибке слушателя уронить приложение
                Log.e(TAG, "Message listener for chat $chatId stopped", e)
            }
            .collect()
    }

    /**
     * 2 пути доставки
     * 1 WorkManager - гарантия
     * 2 быстрый путь - если сеть есть то отправляем и не ждём планировщик
     * оба идут c Mutex, двойной отправки не будет
     */
    private fun sendPendingMessages() {
        runCatching { scheduler.schedule() }
            .onFailure { Log.e(TAG, "Failed to schedule sync", it) }

        if (networkMonitor.isOnline()) {
            appScope.launch { pendingMessageSender.sync() }
        }
    }

    private companion object {
        const val TAG = "MessageRepository"
        const val MAX_SYNC_RETRY_DELAY_MS = 20_000L
    }
}