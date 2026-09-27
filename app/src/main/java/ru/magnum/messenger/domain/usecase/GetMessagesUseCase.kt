package ru.magnum.messenger.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.repository.MessageRepository
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(
        chatId: String
    ): Flow<List<Message>> {
        return repository.getMessages(chatId)
    }
}