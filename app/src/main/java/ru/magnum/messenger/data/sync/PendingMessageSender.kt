package ru.magnum.messenger.data.sync

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.magnum.messenger.data.local.datasource.MessageLocalDataSource
import ru.magnum.messenger.data.local.entity.PendingMessageEntity
import ru.magnum.messenger.data.local.room.mapper.toDomain
import ru.magnum.messenger.data.remote.datasource.MessageRemoteDataSource
import ru.magnum.messenger.data.remote.firebase.isTempError
import javax.inject.Inject
import javax.inject.Singleton

// благодаря mutex нет гонок и двойной отправки, порядок сообщений сохраняется
@Singleton
class PendingMessageSender @Inject constructor(
    private val local: MessageLocalDataSource,
    private val remote: MessageRemoteDataSource,
) {
    enum class SyncResult {
        SUCCESS,
        RETRY
    }

    private val mutex = Mutex()

    // перечитываем очередь, пока она не закончится
    // так подхватываем сообщения которые пользователь дописал пока отправляли предыдущие
    suspend fun sync(): SyncResult = mutex.withLock {
        var pendingMessages = local.getPendingMessages() // отсортированы по createdAt
        while (pendingMessages.isNotEmpty()) {
            for (message in pendingMessages) {
                // Временная ошибка - останавливаемся целиком, чтобы не нарушить порядок
                val isSent = send(message)
                if (!isSent) return@withLock SyncResult.RETRY
            }
            pendingMessages = local.getPendingMessages()
        }
        SyncResult.SUCCESS
    }

    // true - сообщение обработано (отправлено или окончательно провалено)
    // false - временная ошибка, надо повторить позже
    private suspend fun send(pending: PendingMessageEntity): Boolean {
        return try {
            remote.sendMessage(pending.chatId, pending.toDomain())
            local.markSent(pending.id)
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (e.isTempError()) {
                Log.w(TAG, "Temp error sending ${pending.id}, will retry", e)
                false
            } else {
                Log.e(TAG, "Permanent error sending ${pending.id}, marking FAILED", e)
                local.markFailed(pending.id)
                true
            }
        }
    }

    private companion object {
        const val TAG = "PendingMessageSender"
    }
}
