package ru.magnum.messenger.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ru.magnum.messenger.presentation.auth.AuthScreen
import ru.magnum.messenger.presentation.chat.ChatScreen
import ru.magnum.messenger.presentation.splash.SplashScreen
import ru.magnum.messenger.presentation.users.UsersScreen

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash
        ) {
            composable<Screen.Splash> {
                SplashScreen(
                    onAuthorized = {
                        navController.navigate(Screen.Users) {
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
                            Screen.Users
                        )
                    }
                )
            }

            composable<Screen.Users> {
                UsersScreen(
                    onNavigateToChat = { chatId ->
                        navController.navigate(
                            Screen.Chat(chatId)
                        )
                    }
                )
            }

            composable<Screen.Chat> { backStackEntry ->
                val chat = backStackEntry.toRoute<Screen.Chat>()
                ChatScreen(
                    chatId = chat.chatId
                )

            }
        }
    }
}