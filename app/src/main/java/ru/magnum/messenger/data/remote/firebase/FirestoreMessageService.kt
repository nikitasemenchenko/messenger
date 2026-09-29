package ru.magnum.messenger.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                    "createdAt" to message.createdAt,
                    "status" to message.status.name
                )
            )
            .await()
    }

    fun observeMessages(
        chatId: String,
        onUpdate: suspend (List<Message>) -> Unit
    ): ListenerRegistration {
        return firestore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, exception ->
                if(exception != null){
                    return@addSnapshotListener
                }

                val messages = snapshot
                    ?.documents
                    ?.map { document ->
                        Message(
                            id = document.id,
                            senderId = document.getString("senderId") ?: "",
                            text = document.getString("text") ?: "",
                            createdAt = document.getLong("createdAt") ?: 0L,
                            status = MessageStatus.valueOf(
                                document.getString("status") ?: "SENT"
                            )
                        )
                    }
                    ?: emptyList()
                CoroutineScope(
                    Dispatchers.IO
                ).launch {
                    onUpdate(messages)
                }
            }
    }
}