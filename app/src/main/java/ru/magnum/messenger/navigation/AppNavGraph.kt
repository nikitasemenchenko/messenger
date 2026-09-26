package ru.magnum.messenger.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.magnum.messenger.presentation.auth.AuthScreen
import ru.magnum.messenger.presentation.splash.SplashScreen

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash
    ) {
        composable<Screen.Splash> {
            SplashScreen(
                onAuthorized = {
                    navController.navigate(Screen.Chat) {
                        popUpTo(
                            Screen.Splash
                        ) {
                            inclusive = true
                        }
                    }
                },
                onUnauthorized = {
                    navController.navigate(Screen.Auth) {
                        popUpTo(
                            Screen.Splash
                        ) {
                            inclusive = true
                        }
                    }
                }
            )
        }

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