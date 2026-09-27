package ru.magnum.messenger.domain.repository

import ru.magnum.messenger.domain.model.UserProfile

interface UserRepository {
    suspend fun getUsers(): Result<List<UserProfile>>
}