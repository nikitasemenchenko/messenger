package ru.magnum.messenger.presentation.users

import ru.magnum.messenger.domain.model.UserProfile

sealed interface UsersEvent {
    data class UserClicked(
        val user: UserProfile
    ): UsersEvent
}