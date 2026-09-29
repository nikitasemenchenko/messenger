package ru.magnum.messenger.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.magnum.messenger.data.local.room.dao.PendingMessageDao
import ru.magnum.messenger.data.remote.firebase.FirestoreMessageService
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.model.MessageStatus

@HiltWorker
class MessageSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val pendingDao: PendingMessageDao,
    private val messageService: FirestoreMessageService
): CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        val messages = pendingDao.getPendingMessages()

        return try {
            messages.forEach { message ->
                messageService.sendMessage(
                    message.chatId,
                    Message(
                        id = message.id,
                        senderId = message.senderId,
                        text = message.text,
                        createdAt = message.createdAt,
                        status = MessageStatus.SENDING
                    )
                )
                pendingDao.delete(message)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}