package ru.magnum.messenger.data.repository

import ru.magnum.messenger.data.remote.firebase.FirebaseUserService
import ru.magnum.messenger.domain.model.UserProfile
import ru.magnum.messenger.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseUserService: FirebaseUserService
): UserRepository {
    override suspend fun getUsers(): Result<List<UserProfile>> {
        return try {
            Result.success(
                firebaseUserService.getUsers()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}