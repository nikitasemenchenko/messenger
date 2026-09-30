package ru.magnum.messenger.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

//Источник правды о доставке результат самой отправки в Firestore, а не этот флаг
//Если монитор ошибся, страхует WorkManager
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext context: Context
) {
    private val connectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    fun isOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capability = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capability.isUsable()
    }

    val isOnlineFlow: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(networkCapabilities.isUsable())
            }

            override fun onLost(network: Network) {
                trySend(isOnline())
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        trySend(isOnline())
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    private fun NetworkCapabilities.isUsable(): Boolean =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
