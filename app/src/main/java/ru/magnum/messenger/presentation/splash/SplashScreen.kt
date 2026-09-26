package ru.magnum.messenger.presentation.splash

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onAuthorized: () -> Unit,
    onUnauthorized: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    LaunchedEffect(state.value){
        when(state.value){
            SplashState.Unauthorized -> {
                onUnauthorized()
            }

            SplashState.Authorized -> {
                onAuthorized()
            }
            else -> {

            }
        }
    }
    CircularProgressIndicator()
}