package ru.magnum.messenger.domain.usecase

import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(
        chatId: String,
        message: Message
    ): Result<Unit>{
        return repository.sendMessage(chatId, message)
    }
}