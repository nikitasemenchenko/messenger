package ru.magnum.messenger.domain.usecase

import ru.magnum.messenger.domain.repository.MessageRepository
import javax.inject.Inject

class SyncMessagesUseCase @Inject constructor(
    private val repository: MessageRepository
) {
    operator fun invoke(
        chatId: String
    ) {
        repository.syncMessages(chatId)
    }
}