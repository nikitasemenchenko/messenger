package ru.magnum.messenger.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.model.MessageStatus
import javax.inject.Inject

class FirestoreMessageService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun sendMessage(
        chatId: String,
        message: Message
    ) {
        firestore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.id)
            .set(
                mapOf(
                    "senderId" to message.senderId,
                    "text" to message.text,
                    "createdAt" to message.createdAt,
                    "status" to message.status.name
                )
            )
            .await()
    }

    fun observeMessages(
        chatId: String
    ): Flow<List<Message>> = callbackFlow {
        val listener = firestore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages =
                    snapshot?.documents
                        ?.map {
                            Message(
                                id = it.id,
                                senderId = it.getString("senderId") ?: "",
                                text = it.getString("text") ?: "",
                                createdAt = it.getLong("createdAt") ?: 0L,
                                status = MessageStatus.valueOf(it.getString("status") ?: "SENT")
                            )
                        }
                        ?: emptyList()
                trySend(messages)
            }
        awaitClose {
            listener.remove()
        }
    }
}