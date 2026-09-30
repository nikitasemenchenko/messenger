package ru.magnum.messenger.core

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.magnum.messenger.core.di.ApplicationScope
import ru.magnum.messenger.data.local.datasource.MessageLocalDataSource
import ru.magnum.messenger.data.network.NetworkMonitor
import ru.magnum.messenger.data.sync.MessageSyncScheduler
import ru.magnum.messenger.data.sync.PendingMessageSender
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltAndroidApp
class MessengerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var localDataSource: MessageLocalDataSource

    @Inject
    lateinit var pendingMessageSender: PendingMessageSender

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var syncScheduler: dagger.Lazy<MessageSyncScheduler>

    @Inject
    @ApplicationScope
    lateinit var appScope: CoroutineScope

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Страховка после убийства процесса
        // очередь не пуста - ставим задачу в WorkManager
        appScope.launch {
            if (localDataSource.pendingCount() > 0) {
                syncScheduler.get().schedule()
            }
        }

        appScope.launch {
            networkMonitor.isOnlineFlow
                .filter { it }
                .collect {
                    try {
                        pendingMessageSender.sync()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e("MessengerApp", "Pending messages sending failed", e)
                    }
                }
        }
    }
}
