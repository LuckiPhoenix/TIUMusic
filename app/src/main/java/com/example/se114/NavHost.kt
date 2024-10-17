package com.example.se114

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.se114.Login.LoginScreen
import com.example.se114.Login.RecoverPasswordScreen
import com.example.se114.Login.RegisterScreen
import com.example.se114.Login.ResetPasswordScreen

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