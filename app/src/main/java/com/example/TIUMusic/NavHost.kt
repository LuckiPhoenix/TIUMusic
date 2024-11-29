package com.example.TIUMusic

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.TIUMusic.Libs.Visualizer.VisualizerViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeView
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeViewModel
import com.example.TIUMusic.Login.LoginScreen
import com.example.TIUMusic.Login.RecoverPasswordScreen
import com.example.TIUMusic.Login.RegisterScreen
import com.example.TIUMusic.Login.ResetPasswordScreen
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.Screens.HomeScreen
import com.example.TIUMusic.Screens.LibraryScreen
import com.example.TIUMusic.Screens.NewScreen
import com.example.TIUMusic.Screens.NowPlayingSheet
import com.example.TIUMusic.Screens.PlaylistScreen
import com.example.TIUMusic.Screens.SearchScreen
import com.example.TIUMusic.SongData.PlayerViewModel

@Composable
fun NavHost(
    playerViewModel: PlayerViewModel,
    visualizerViewModel: VisualizerViewModel,
    youtubeViewModel: YoutubeViewModel
) {
    val context = LocalContext.current;
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    Log.d("NavHost", "Current route: $currentRoute")

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "auth"
        ) {
            navigation(startDestination = "login", route = "auth") {
                composable("login") { LoginScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("reset") { ResetPasswordScreen(navController) }
                composable(
                    route = "recover/{email}",
                    arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                    RecoverPasswordScreen(
                        navController,
                        email = backStackEntry.arguments?.getString("email") ?: ""
                    )
                }
            }

            navigation(startDestination = "home", route = "main") {
                composable("home") {
                    HomeScreen(
                        navController = navController,
                        onTabSelected = { tabIndex ->
                            when (tabIndex) {
                                0 -> {} // Currently on home
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        onPlaylistClick = { musicItem ->
                            navController.navigate("playlist/${musicItem.id}")
                        }
                    )
                }

                composable("new") { NewScreen(
                    navController,
                    onTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> {navController.navigate("home")}
                        1 -> {}
                        2 -> navController.navigate("library")
                        3 -> navController.navigate("search")
                    }
                    },onPlaylistClick = { musicItem ->
                        navController.navigate("playlist/${musicItem.id}")
                    }
                    ) }
                composable("search") { SearchScreen(
                    navController,
                    onTabSelected = { tabIndex ->
                        when (tabIndex) {
                            0 -> navController.navigate("home")
                            1 -> navController.navigate("new")
                            2 -> navController.navigate("library")
                            3 -> {}
                        }
                    }) }
                composable("library") { LibraryScreen(navController,
                    onTabSelected = { tabIndex ->
                        when (tabIndex) {
                            0 -> navController.navigate("home")
                            1 -> navController.navigate("new")
                            2 -> {}
                            3 -> navController.navigate("search")
                        }
                    }) }
                composable(
                    route = "playlist/{playlistId}",
                    arguments = listOf(navArgument("playlistId") { type = NavType.StringType })
                ) { backStackEntry ->
                    PlaylistScreen(
                        navController = navController,
                        playlistId = backStackEntry.arguments?.getString("playlistId") ?: "",
                        onTabSelected ={ tabIndex ->
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                    )
                }
            }
        }

        // Check if we're in any route within the main navigation
        if (currentBackStackEntry?.destination?.parent?.route == "main") {
            NowPlayingSheet(
                playerViewModel = playerViewModel,
                youtubeViewModel = youtubeViewModel,
                visualizerViewModel = visualizerViewModel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp),
            )
            Log.d("NavHost", "Overlay shown")
        }
    }
}