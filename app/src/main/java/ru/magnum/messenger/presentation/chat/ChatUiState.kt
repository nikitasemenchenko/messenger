package ru.magnum.messenger.presentation.chat

import ru.magnum.messenger.domain.model.Message

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val messageText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)