package ru.magnum.messenger.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.magnum.messenger.domain.model.UserProfile
import ru.magnum.messenger.domain.usecase.GetOrCreateChatUseCase
import ru.magnum.messenger.domain.usecase.GetCurrentUserUseCase
import ru.magnum.messenger.domain.usecase.GetUsersUseCase
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getOrCreateChatUseCase: GetOrCreateChatUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {
    private val _state = MutableStateFlow(UsersState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<UsersEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = getUsersUseCase()

            result.onSuccess { users ->
                _state.value = _state.value.copy(
                    users = users,
                    isLoading = false,
                    errorMessage = null
                )
            }
            result.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = error.message
                )
            }
        }
    }

    fun onEvent(
        event: UsersEvent
    ){
        when(event){
            is UsersEvent.UserClicked -> {
                createChat(
                    event.user
                )
            }
        }
    }

    private fun createChat(
        user: UserProfile
    ){
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase() ?: return@launch

            val result = getOrCreateChatUseCase(
                listOf(
                    currentUser.id,
                    user.uid
                )
            )
            result.onSuccess { chat ->
                _effect.emit(
                    UsersEffect.NavigateToChat(
                        chat.id
                    )
                )
            }
        }
    }
}