package ru.magnum.messenger.presentation.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.magnum.messenger.domain.model.MessageStatus

@Composable
fun ChatScreen(
    chatId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(chatId) {
        viewModel.observeMessages(chatId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(state.messages, key = {it.id}) { message ->
                // Временный UI, только чтобы можно было проверить статусы и retry
                val status = when (message.status) {
                    MessageStatus.SENDING -> "  (отправляется…)"
                    MessageStatus.FAILED -> "  (не отправлено, нажмите чтобы повторить)"
                    MessageStatus.SENT -> ""
                }
                Text(
                    text = message.text + status,
                    modifier = Modifier
                        .padding(12.dp)
                        .then(
                            if (message.status == MessageStatus.FAILED) {
                                Modifier.clickable { viewModel.retryMessage(message.id) }
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = state.messageText,
                onValueChange = {
                    viewModel.onMessageChanged(it)
                },
                label = {
                    Text("Message")
                },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    viewModel.sendMessage(chatId)
                }
            ) {
                Text("Send")
            }
        }
    }
}