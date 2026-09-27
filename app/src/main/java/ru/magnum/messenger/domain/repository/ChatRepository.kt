package ru.magnum.messenger.domain.repository

import ru.magnum.messenger.domain.model.Chat

interface ChatRepository {
    suspend fun getOrCreateChat(
        participants: List<String>
    ): Result<Chat>
}