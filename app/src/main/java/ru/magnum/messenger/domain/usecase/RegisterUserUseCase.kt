package ru.magnum.messenger.domain.usecase

import ru.magnum.messenger.domain.model.UserProfile
import ru.magnum.messenger.domain.repository.AuthRepository
import ru.magnum.messenger.domain.repository.ProfileRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String
    ): Result<Unit>{
        val authResult = authRepository.register(email, password)
        if(authResult.isFailure){
            return Result.failure(
                authResult.exceptionOrNull()!!
            )
        }
        val user = authResult.getOrNull()
            ?: return Result.failure(Exception("User is null"))

        val userProfile = UserProfile(
            uid = user.id,
            email = user.email,
            username = username,
            avatarUrl = null,
            createdAt = System.currentTimeMillis()
        )
        return profileRepository.createProfile(userProfile)
    }
}