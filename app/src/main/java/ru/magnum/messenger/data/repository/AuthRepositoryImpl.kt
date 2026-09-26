package ru.magnum.messenger.data.repository

import ru.magnum.messenger.data.mapper.toDomain
import ru.magnum.messenger.data.remote.firebase.FirebaseAuthService
import ru.magnum.messenger.domain.model.User
import ru.magnum.messenger.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService
): AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val firebaseUser = firebaseAuthService.login(email, password)
            Result.success(firebaseUser.toDomain())
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val firebaseUser = firebaseAuthService.register(email, password)
            Result.success(firebaseUser.toDomain())
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {

    }

    override fun getCurrentUser(): User? {
        return firebaseAuthService.getCurrentUser()?.toDomain()
    }

}