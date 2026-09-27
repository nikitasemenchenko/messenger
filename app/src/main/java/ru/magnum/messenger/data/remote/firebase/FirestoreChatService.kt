package ru.magnum.messenger.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreChatService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getOrCreateChat(
        participants: List<String>
    ): String {
        val query = firestore
            .collection("chats")
            .whereArrayContains(
                "participants",
                participants.first()
            )
            .get()
            .await()

        val existingChat = query.documents.firstOrNull {
            val users = it.get("participants") as? List<String>
            users?.containsAll(participants) ?: false
        }
        if(existingChat != null) {
            return existingChat.id
        }

        val document = firestore
            .collection("chats")
            .document()

        document.set(
            mapOf(
                "participants" to participants,
                "createdAt" to System.currentTimeMillis()
            )
        )
            .await()

        return document.id
    }


}