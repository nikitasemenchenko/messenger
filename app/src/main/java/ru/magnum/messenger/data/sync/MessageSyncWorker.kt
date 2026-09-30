package ru.magnum.messenger.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class MessageSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val pendingMessageSender: PendingMessageSender,
): CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        return try {
            when (pendingMessageSender.sync()) {
                PendingMessageSender.SyncResult.SUCCESS -> Result.success()
                PendingMessageSender.SyncResult.RETRY -> Result.retry()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("MessageSyncWorker", "Error while sending pending messages", e)
            Result.retry()
        }
    }
}