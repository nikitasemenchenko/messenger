package ru.magnum.messenger.presentation.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel(),
    onNavigateToChat: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is UsersEffect.NavigateToChat -> {
                    onNavigateToChat(
                        effect.chatId
                    )
                }
            }
        }
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        state.errorMessage != null -> {
            Text(
                text = state.errorMessage!!
            )
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(16.dp),
            ) {
                items(state.users) { user ->
                    Text(
                        text = user.username,
                        modifier = Modifier.padding(16.dp)
                            .clickable {
                                viewModel.onEvent(
                                    UsersEvent.UserClicked(user)
                                )
                            }
                    )
                }
            }
        }
    }
}