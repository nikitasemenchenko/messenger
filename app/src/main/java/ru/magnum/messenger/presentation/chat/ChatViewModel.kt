package ru.magnum.messenger.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.magnum.messenger.domain.model.Message
import ru.magnum.messenger.domain.model.MessageStatus
import ru.magnum.messenger.domain.usecase.GetCurrentUserUseCase
import ru.magnum.messenger.domain.usecase.GetMessagesUseCase
import ru.magnum.messenger.domain.usecase.RetryMessageUseCase
import ru.magnum.messenger.domain.usecase.SendMessageUseCase
import ru.magnum.messenger.domain.usecase.SyncMessagesUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val retryMessageUseCase: RetryMessageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val syncMessagesUseCase: SyncMessagesUseCase
): ViewModel() {
    private val _state = MutableStateFlow(ChatUiState())
    val state = _state.asStateFlow()

    private var observedChatId: String? = null
    private var messagesJob: Job? = null
    private var syncJob: Job? = null

    fun observeMessages(
        chatId: String
    ) {
        if (observedChatId == chatId) return
        observedChatId = chatId

        messagesJob?.cancel()
        syncJob?.cancel()

        messagesJob = viewModelScope.launch {
                getMessagesUseCase(chatId).collect { messages ->
                    _state.update { it.copy(messages = messages) }
                    }
            }

        syncJob = viewModelScope.launch {
            syncMessagesUseCase(chatId)
        }
    }

    fun onMessageChanged(
        text: String
    ) {
        _state.value = _state.value.copy(
            messageText = text
        )
    }

    fun sendMessage(
        chatId: String
    ) {
        val text = _state.value.messageText.trim()

        if(text.isBlank()) {
            return
        }

        val user = getCurrentUserUseCase() ?: return

        // Поле очищаем сразу, не ждём никакой сети
        _state.update {
            it.copy(
                messageText = "",
                errorMessage = null)
        }

        viewModelScope.launch {
            val message = Message(
                id = UUID.randomUUID().toString(),
                senderId = user.id,
                text = text,
                createdAt = System.currentTimeMillis(),
                status = MessageStatus.SENDING
            )

            sendMessageUseCase(chatId, message).onFailure {
                // Не смогли даже записать в локальную БД - возвращаем текст пользователю
                _state.update { s ->
                    s.copy(
                        messageText = text,
                        errorMessage = "Не удалось сохранить сообщение"
                    )
                }
            }
        }
    }

    fun retryMessage(
        messageId: String
    ) {
        viewModelScope.launch {
            retryMessageUseCase(messageId)
        }
    }
}