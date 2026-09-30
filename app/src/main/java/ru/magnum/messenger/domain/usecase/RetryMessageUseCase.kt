package ru.magnum.messenger.domain.usecase

import ru.magnum.messenger.domain.repository.MessageRepository
import javax.inject.Inject

class RetryMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(
        messageId: String
    ): Result<Unit> {
        return repository.retryMessage(messageId)
    }
}
