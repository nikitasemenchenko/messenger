package ru.magnum.messenger.domain.usecase

import ru.magnum.messenger.domain.model.Chat
import ru.magnum.messenger.domain.repository.ChatRepository
import javax.inject.Inject

class GetOrCreateChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        participants: List<String>
    ): Result<Chat> {
        return chatRepository.getOrCreateChat(participants)
    }
}