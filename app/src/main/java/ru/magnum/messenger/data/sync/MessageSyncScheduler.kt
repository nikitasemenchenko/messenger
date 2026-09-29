package ru.magnum.messenger.data.sync

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
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
            .build()
        workManager.enqueue(
            request
        )
    }
}