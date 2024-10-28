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
            HomeScreen()
        }
    }
}