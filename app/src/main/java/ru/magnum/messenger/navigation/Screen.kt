package ru.magnum.messenger.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Splash: Screen

    @Serializable
    data object Auth: Screen

    @Serializable
    data object Chat: Screen
}