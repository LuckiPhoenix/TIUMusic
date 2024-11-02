package com.example.TIUMusic

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.TIUMusic.Login.LoginScreen
import com.example.TIUMusic.Login.RecoverPasswordScreen
import com.example.TIUMusic.Login.RegisterScreen
import com.example.TIUMusic.Login.ResetPasswordScreen
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.Screens.HomeScreen
import com.example.TIUMusic.Screens.LibraryScreen
import com.example.TIUMusic.Screens.NewScreen
import com.example.TIUMusic.Screens.PlaylistScreen
import com.example.TIUMusic.Screens.SearchScreen

@Composable
fun NavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("reset") {
            ResetPasswordScreen(navController)
        }
        composable(
            route = "recover/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) {
            RecoverPasswordScreen(navController)
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> {} //Đang ở home ko quan tâm
                        1 -> navController.navigate("new")
                        2 -> navController.navigate("library")
                        3 -> navController.navigate("search")
                    }
                },
                onPlaylistClick = { musicItem ->
                    navController.navigate("player/${musicItem.id}")
                }
            )
            composable("new") {
                NewScreen(navController = navController)
            }
            composable("search") {
                SearchScreen(navController = navController)
            }
            composable("library") {
                LibraryScreen(navController = navController)
            }
            composable(
                route = "playlist/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
            ) {
                PlaylistScreen(
                    navController = navController,
                    playlistId = it.arguments?.getString("playlistId") ?: ""
                )
            }
            //TODO: PlayScreen
        }
    }
}