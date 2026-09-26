package ru.magnum.messenger.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.magnum.messenger.domain.usecase.LoginUseCase
import ru.magnum.messenger.domain.usecase.RegisterUserUseCase
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUserUseCase: RegisterUserUseCase
): ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    fun onEvent(
        event: AuthEvent
    ) {
        when(event) {
            is AuthEvent.EmailChanged -> {
                _state.value = _state.value.copy(
                    email = event.email
                )
            }
            is AuthEvent.PasswordChanged -> {
                _state.value =
                    _state.value.copy(
                        password = event.password
                    )
            }
            AuthEvent.LoginClicked -> {
                login()
            }

            AuthEvent.RegisterClicked -> {
                register()
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = loginUseCase(
                _state.value.email,
                _state.value.password
            )
            result.onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true
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

    fun register() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = registerUserUseCase(
                _state.value.email,
                _state.value.password,
                _state.value.email.substringBefore("@")
            )
            result.onSuccess {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true
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
}