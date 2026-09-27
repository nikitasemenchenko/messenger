package ru.magnum.messenger.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import ru.magnum.messenger.domain.model.Message
import javax.inject.Inject

class FirestoreMessageService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun sendMessage(
        chatId: String,
        message: Message
    ){
        firestore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.id)
            .set(
                mapOf(
                    "senderId" to message.senderId,
                    "text" to message.text,
                    "createdAt" to message.createdAt
                )
            )
            .await()
    }

    fun getMessages(
        chatId: String
    ): Flow<List<Message>> = callbackFlow {
        val listener = firestore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, exception ->
                if(exception != null){
                    close(exception)
                    return@addSnapshotListener
                }

                val messages = snapshot
                    ?.documents
                    ?.map { document ->
                        Message(
                            id = document.id,
                            senderId = document.getString("senderId") ?: "",
                            text = document.getString("text") ?: "",
                            createdAt = document.getLong("createdAt") ?: 0L
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