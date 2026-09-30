package ru.magnum.messenger.data.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MessageSyncScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun schedule(){
        val request = OneTimeWorkRequestBuilder<MessageSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.SECONDS
            )
            .build()
        // Unique work - не плодим десятки параллельных воркеров
        // APPEND_OR_REPLACE - если воркер уже работает, новая задача встанет после него
        workManager.enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            request
        )
    }

    private companion object {
        const val UNIQUE_WORK_NAME = "send_pending_messages"
    }
}