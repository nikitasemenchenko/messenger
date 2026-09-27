package ru.magnum.messenger.data.repository

import ru.magnum.messenger.data.remote.firebase.FirestoreChatService
import ru.magnum.messenger.domain.model.Chat
import ru.magnum.messenger.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestoreChatService: FirestoreChatService
): ChatRepository {

    override suspend fun getOrCreateChat(participants: List<String>): Result<Chat> {
        return try {
            val id = firestoreChatService.getOrCreateChat(participants)
            Result.success(
                Chat(
                    id = id,
                    participants = participants,
                    createdAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}