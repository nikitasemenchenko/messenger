package ru.magnum.messenger.presentation.users

import ru.magnum.messenger.domain.model.UserProfile

data class UsersState(
    val users: List<UserProfile> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)