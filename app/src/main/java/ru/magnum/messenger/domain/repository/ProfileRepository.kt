package ru.magnum.messenger.domain.repository

import ru.magnum.messenger.domain.model.UserProfile

interface ProfileRepository {
    suspend fun createProfile(
        profile: UserProfile
    ): Result<Unit>

    suspend fun getProfile(
        uid: String
    ): Result<UserProfile?>
}