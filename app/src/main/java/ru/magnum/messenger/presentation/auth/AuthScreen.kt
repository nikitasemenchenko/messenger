package ru.magnum.messenger.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSuccess: () -> Unit
){
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSuccess) {

        if(state.isSuccess) {
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        OutlinedTextField(
            value = state.email,
            onValueChange = {
                viewModel.onEvent(
                    AuthEvent.EmailChanged(it)
                )
            },
            label = {
                Text("Email")
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = {
                viewModel.onEvent(
                    AuthEvent.PasswordChanged(it)
                )
            },
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {
                viewModel.onEvent(
                    AuthEvent.LoginClicked
                )
            }
        ) {
            Text("Login")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = {
                viewModel.onEvent(
                    AuthEvent.RegisterClicked
                )
            }
        ) {
            Text("Register")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if(state.isLoading) {
            CircularProgressIndicator()
        }
        state.errorMessage?.let { error ->
            Text(
                text = error
            )
        }
    }
}