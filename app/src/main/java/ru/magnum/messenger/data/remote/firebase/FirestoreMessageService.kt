package ru.magnum.messenger.data.remote.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.model.MessageStatus
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class FirestoreMessageService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun messages(chatId: String): CollectionReference =
        firestore.collection("chats").document(chatId).collection("messages")

    suspend fun sendMessage(
        chatId: String,
        message: Message
    ) {
        // Статус доставки не храню в Firestore
        val data = mapOf(
            "senderId" to message.senderId,
            "text" to message.text,
            "createdAt" to message.createdAt
        )
        try {
            withTimeout(SEND_TIMEOUT_MS.milliseconds) {
                messages(chatId).document(message.id).set(data).await()
            }
        } catch (e: TimeoutCancellationException) {
            throw IOException("Server is not responding within $SEND_TIMEOUT_MS ms", e)
        }
    }

    // Эмитит только изменившиеся и уже подтвержденные сервером сообщения.
    // не перезаписываю в Room всю историю на каждый снапшот
    fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val snapshot = messages(chatId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener

            val changed = snapshot.documentChanges
                .filter { it.type != DocumentChange.Type.REMOVED }
                .map { it.document }
                .filter { !it.metadata.hasPendingWrites() }
                .mapNotNull { it.toMessageOrNull() }

            if (changed.isNotEmpty()) {
                trySend(changed)
            }
        }
        awaitClose { snapshot.remove() }
    }

    private fun DocumentSnapshot.toMessageOrNull(): Message? {
        val senderId = getString("senderId") ?: return null
        val text = getString("text") ?: return null
        val createdAt = getLong("createdAt") ?: return null
        return Message(
            id = id,
            senderId = senderId,
            text = text,
            createdAt = createdAt,
            status = MessageStatus.SENT
        )
    }

    private companion object {
        const val SEND_TIMEOUT_MS = 15_000L
    }

}