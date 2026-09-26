package ru.magnum.messenger.data.repository

import ru.magnum.messenger.data.remote.firebase.FirebaseUserService
import ru.magnum.messenger.domain.model.UserProfile
import ru.magnum.messenger.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firebaseUserService: FirebaseUserService
): ProfileRepository {

    override suspend fun createProfile(profile: UserProfile): Result<Unit> {
        return try {
            firebaseUserService.createUserProfile(profile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfile(uid: String): Result<UserProfile?> {
        return try {
            Result.success(
                firebaseUserService.getProfile(uid)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}