package ru.magnum.messenger.presentation.auth

sealed interface AuthEvent {

    data class EmailChanged(
        val email: String
    ): AuthEvent

    data class PasswordChanged (
        val password: String
    ): AuthEvent

    data object LoginClicked: AuthEvent

    data object RegisterClicked: AuthEvent
}