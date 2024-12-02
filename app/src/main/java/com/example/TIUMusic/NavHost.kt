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
import com.example.TIUMusic.Login.LoginScreen
import com.example.TIUMusic.Login.RecoverPasswordScreen
import com.example.TIUMusic.Login.RegisterScreen
import com.example.TIUMusic.Login.ResetPasswordScreen
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeLogin
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.Screens.AlbumScreen
import com.example.TIUMusic.Screens.ArtistPage
import com.example.TIUMusic.Screens.HomeScreen
import com.example.TIUMusic.Screens.LibraryScreen
import com.example.TIUMusic.Screens.NewScreen
import com.example.TIUMusic.Screens.NowPlayingSheet
import com.example.TIUMusic.Screens.PlaylistScreen
import com.example.TIUMusic.Screens.SearchScreen
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.PlayerViewModel

@Composable
fun NavHost(
    playerViewModel: PlayerViewModel,
    visualizerViewModel: VisualizerViewModel
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
                        onItemClick = { musicItem ->
                            if(musicItem.type == 0){
                                Log.d("LogNav", "TYPE = 0")
                                playerViewModel.resetPlaylist();
                                playerViewModel.playSong(musicItem, context)
                            } else if(musicItem.type == 1){
                                navController.currentBackStackEntry?.savedStateHandle?.set("title", musicItem.title)
                                navController.currentBackStackEntry?.savedStateHandle?.set("artist", musicItem.artist)
                                navController.currentBackStackEntry?.savedStateHandle?.set("image", musicItem.imageUrl)
                                navController.navigate("playlist/${musicItem.playlistId}")
                                Log.d("LogNav", "TYPE = 1 with ${musicItem.playlistId}")
                            }
                            else if(musicItem.type == 2){
                                navController.navigate("album/${musicItem.browseId}")
                                Log.d("LogNav", "TYPE = 2")
                            }
                        }
                    )
                }
                composable("new") {
                    NewScreen(
                        navController,
                        onTabSelected = { tabIndex ->
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> {}
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        }, onItemClick = { musicItem ->
                            if(musicItem.type == 0){
                                Log.d("LogNav", "TYPE = 0")
                                playerViewModel.resetPlaylist();
                                playerViewModel.playSong(musicItem, context)
                            } else if(musicItem.type == 1){
                                navController.currentBackStackEntry?.savedStateHandle?.set("title", musicItem.title)
                                navController.currentBackStackEntry?.savedStateHandle?.set("artist", musicItem.artist)
                                navController.currentBackStackEntry?.savedStateHandle?.set("image", musicItem.imageUrl)
                                navController.navigate("playlist/${musicItem.playlistId}")
                                Log.d("LogNav", "TYPE = 1 with ${musicItem.playlistId}")
                            }
                            else if(musicItem.type == 2){
                                navController.navigate("album/${musicItem.browseId}")
                                Log.d("LogNav", "TYPE = 2")
                            }
                        },
                        hiltViewModel(),
                        playerViewModel = playerViewModel,
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
                        },
                        onClick = {
                            playerViewModel.resetPlaylist();
                            playerViewModel.playSong(it, context);
                        }
                    )
                }
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
                    arguments = listOf(
                        navArgument("playlistId") { type = NavType.StringType },
                    )
                ) { backStackEntry ->
                    val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""

                    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
                    val title = savedStateHandle?.get<String>("title") ?: ""
                    val artist = savedStateHandle?.get<String>("artist") ?: ""
                    val image = savedStateHandle?.get<String>("image") ?: ""
                    PlaylistScreen(
                        navController = navController,
                        playlistItem = MusicItem(
                            videoId = "",
                            title = title,
                            artist = artist,
                            imageUrl = image,
                            type = 1,
                            playlistId = playlistId,
                        ),
                        onTabSelected = { tabIndex ->
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        onSongClick = { musicItem, index, playlist ->
                            Log.d("LogNav", "TYPE = 0")
                            playerViewModel.setPlaylist(playlist);
                            playerViewModel.setIsShuffled(false);
                            playerViewModel.playSongInPlaylistAtIndex(index, context);
                        },
                        onShuffleClick = { playlist ->
                            playerViewModel.setPlaylist(playlist);
                            playerViewModel.shufflePlaylist();
                            playerViewModel.playSongInPlaylistAtIndex(0, context);
                        },
                        onPlayClick = { playlist ->
                            playerViewModel.setPlaylist(playlist);
                            playerViewModel.setIsShuffled(false);
                            playerViewModel.playSongInPlaylistAtIndex(0, context);
                        }
                    )
                }
                composable(
                    route = "album/{albumId}",
                    arguments = listOf(
                        navArgument("albumId") { type = NavType.StringType },
                    )
                ){backStackEntry ->
                    val browseId = backStackEntry.arguments?.getString("albumId") ?: ""
                    AlbumScreen(
                        navController = navController,
                        albumId = browseId,
                        onTabSelected = { tabIndex ->
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        onSongClick = { musicItem, index, playlist ->
                            Log.d("LogNav", "TYPE = 0")
                            playerViewModel.setPlaylist(playlist);
                            playerViewModel.setIsShuffled(false);
                            playerViewModel.playSongInPlaylistAtIndex(index, context);
                        },
                        onShuffleClick = { playlist ->
                            playerViewModel.setPlaylist(playlist);
                            playerViewModel.shufflePlaylist();
                            playerViewModel.playSongInPlaylistAtIndex(0, context);
                        },
                        onPlayClick = { playlist ->
                            playerViewModel.setPlaylist(playlist);
                            playerViewModel.setIsShuffled(false);
                            playerViewModel.playSongInPlaylistAtIndex(0, context);
                        }
                    )
                }
                composable(
                    route = "artist",
                ) {
                    ArtistPage(
                        BrowseID = "",
                        onClickMusicItem = {},
                        onClickAlbum = {},
                        onTabSelected = { tabIndex ->
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        ytmusicViewModel = hiltViewModel(),
                        navController = navController,
                    )
                }
            }
        }

        // Check if we're in any route within the main navigation
        if (currentBackStackEntry?.destination?.parent?.route == "main") {
            NowPlayingSheet(
                playerViewModel = playerViewModel,
                visualizerViewModel = visualizerViewModel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            )
            Log.d("NavHost", "Overlay shown")
        }
    }
}
