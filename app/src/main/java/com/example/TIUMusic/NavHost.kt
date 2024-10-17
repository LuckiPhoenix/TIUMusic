package com.example.TIUMusic

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.TIUMusic.Login.LoginScreen
import com.example.TIUMusic.Login.RecoverPasswordScreen
import com.example.TIUMusic.Login.RegisterScreen
import com.example.TIUMusic.Login.ResetPasswordScreen

@Composable
fun NavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login"){
        composable("login") {
            LoginScreen( navController = navController )
        }
        composable("register") {
            RegisterScreen( navController = navController )
        }
        composable("reset") {
            ResetPasswordScreen( navController = navController )
        }
        composable("recover") {
            RecoverPasswordScreen(navController = navController)
        }
        composable("Home"){

        }
    }

}