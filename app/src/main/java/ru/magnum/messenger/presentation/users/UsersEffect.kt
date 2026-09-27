package ru.magnum.messenger.presentation.users

sealed interface UsersEffect {
    data class NavigateToChat(
        val chatId: String
    ): UsersEffect
}