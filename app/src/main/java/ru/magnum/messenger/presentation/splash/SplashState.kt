package ru.magnum.messenger.presentation.splash

sealed interface SplashState {
    data object Loading: SplashState
    data object Authorized: SplashState
    data object Unauthorized: SplashState
}