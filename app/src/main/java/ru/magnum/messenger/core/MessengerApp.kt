package ru.magnum.messenger.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MessengerApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}