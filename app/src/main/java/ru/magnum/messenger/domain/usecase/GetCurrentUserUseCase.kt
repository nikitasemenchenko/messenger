package ru.magnum.messenger.domain.usecase

import ru.magnum.messenger.domain.model.User
import ru.magnum.messenger.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}