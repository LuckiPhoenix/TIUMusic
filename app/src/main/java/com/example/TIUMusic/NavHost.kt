package com.example.TIUMusic

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.TIUMusic.Libs.Visualizer.VisualizerViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeMetadata
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeViewModel
import com.example.TIUMusic.Login.LoginScreen
import com.example.TIUMusic.Login.RecoverPasswordScreen
import com.example.TIUMusic.Login.RegisterScreen
import com.example.TIUMusic.Login.ResetPasswordScreen
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeLogin
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
    val userViewModel: UserViewModel = viewModel()
    val startDestination = if (userViewModel.isLoggedIn()) "main" else "auth"

    Log.d("NavHost", "Current route: $currentRoute")

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            navigation(startDestination = "youtubeLogin", route = "auth") {
                composable("youtubeLogin") { YoutubeLogin(navController, userViewModel) }
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

            navigation(startDestination = "youtubeLogin", route = "main") {
                composable("youtubeLogin") { YoutubeLogin(navController, userViewModel) }
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
                            if(musicItem.type == 0){
                                Log.d("LogNav", "TYPE = 0")
                                playerViewModel.setMusicItem(musicItem)
                                youtubeViewModel.loadAndPlayVideo(
                                    videoId = musicItem.videoId,
                                    metadata = YoutubeMetadata(
                                        title = musicItem.title,
                                        artist = musicItem.artist,
                                        artBitmapURL = musicItem.imageUrl,
                                        displayTitle = musicItem.title,
                                        displaySubtitle = musicItem.artist
                                    ),
                                    0L,
                                    context = context
                                );
                            } else if(musicItem.type == 1){
                                navController.currentBackStackEntry?.savedStateHandle?.set("title", musicItem.title)
                                navController.currentBackStackEntry?.savedStateHandle?.set("artist", musicItem.artist)
                                navController.currentBackStackEntry?.savedStateHandle?.set("image", musicItem.imageUrl)
                                navController.navigate("playlist/${musicItem.videoId}")
                                Log.d("LogNav", "TYPE = 1 with ${musicItem.videoId}")
                            }
                            else if(musicItem.type == 2){
                                Log.d("LogNav", "TYPE = 2")
                            }
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
                        navController.navigate("playlist/${musicItem.videoId}")
                    },
                    hiltViewModel()
                    )
                }
                composable("search") { SearchScreen(
                    navController,
                    onTabSelected = { tabIndex ->
                        when (tabIndex) {
                            0 -> navController.navigate("home")
                            1 -> navController.navigate("new")
                            2 -> navController.navigate("library")
                            3 -> navController.navigate("search")
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
                    .padding(bottom = 80.dp)
            )
            Log.d("NavHost", "Overlay shown")
        }
    }
}