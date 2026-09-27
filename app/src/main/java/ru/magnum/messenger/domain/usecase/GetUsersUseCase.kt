package ru.magnum.messenger.domain.usecase

import ru.magnum.messenger.domain.model.UserProfile
import ru.magnum.messenger.domain.repository.AuthRepository
import ru.magnum.messenger.domain.repository.UserRepository
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
){
    suspend operator fun invoke(): Result<List<UserProfile>> {
        val currentUser = authRepository.getCurrentUser()
            ?: return Result.failure(Exception("Error"))
        return userRepository.getUsers().map { users ->
            users.filter {
                it.uid != currentUser.id
            }
        }
    }
}