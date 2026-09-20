package ru.magnum.messenger.domain.usecase

import ru.magnum.messenger.domain.model.User
import ru.magnum.messenger.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User>{
        return authRepository.register(
            email,
            password
        )
    }
}