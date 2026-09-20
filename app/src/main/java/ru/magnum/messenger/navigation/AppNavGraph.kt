package ru.magnum.messenger.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.magnum.messenger.presentation.auth.AuthScreen

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Auth
    ) {
        composable<Screen.Auth> {
            AuthScreen(
                onSuccess = {
                    navController.navigate(
                        Screen.Chat
                    )
                }
            )
        }

        composable<Screen.Chat>{

        }
    }
}