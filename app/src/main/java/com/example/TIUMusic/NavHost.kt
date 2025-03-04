package com.example.TIUMusic

import android.util.Log
import android.webkit.CookieManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.TIUMusic.Libs.Visualizer.VisualizerViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YouTube
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeLogin
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Login.LoginScreen
import com.example.TIUMusic.Login.Playlist
import com.example.TIUMusic.Login.RecoverPasswordScreen
import com.example.TIUMusic.Login.RegisterScreen
import com.example.TIUMusic.Login.ResetPasswordScreen
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.Screens.AlbumScreen
import com.example.TIUMusic.Screens.ArtistPage
import com.example.TIUMusic.Screens.EditPlaylistScreen
import com.example.TIUMusic.Screens.HomeScreen
import com.example.TIUMusic.Screens.LibraryScreen
import com.example.TIUMusic.Screens.MoodListScreen
import com.example.TIUMusic.Screens.NewScreen
import com.example.TIUMusic.Screens.NowPlayingSheet
import com.example.TIUMusic.Screens.PersonalPlaylistScreen
import com.example.TIUMusic.Screens.PlaylistScreen
import com.example.TIUMusic.Screens.SearchScreen
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.PlayerViewModel
import com.google.gson.Gson

@Composable
fun NavHost(
    playerViewModel: PlayerViewModel,
    visualizerViewModel: VisualizerViewModel,
    ytmusicViewModel: YtmusicViewModel
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val userViewModel: UserViewModel = viewModel()
    var startDestination = if (userViewModel.isLoggedIn()) "main" else "auth"


    if (userViewModel.isLoggedIn() && YouTube.cookie == null) {
        CookieManager.getInstance().getCookie(context.getString(R.string.YOUTUBE_MUSIC_URL))?.let {
            YouTube.cookie = it
        }
    }

    Log.d("NavHost", "Current route: $currentRoute")

    val musicItem by playerViewModel.musicItem.collectAsState()
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

            navigation(startDestination = "home", route = "main") {
                composable("home") {
                    HomeScreen(
                        navController = navController,
                        ytMusicViewModel = ytmusicViewModel,
                        onTabSelected = { tabIndex ->
                            playerViewModel.setShouldExpand(-1);
                            when (tabIndex) {
                                0 -> {
                                    ytmusicViewModel.resetHome()
                                    ytmusicViewModel.getHomeContinuation(context)
                                } // Currently on home
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        onItemClick = { musicItem ->
                            if(musicItem.type == 0){
                                Log.d("LogNav", "TYPE = 0")
                                playerViewModel.resetPlaylist()
                                playerViewModel.setRadio(musicItem)
                                playerViewModel.playSong(musicItem, context, true)
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
                            playerViewModel.setShouldExpand(-1);
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> {
                                    ytmusicViewModel.resetNewScreen()
                                    ytmusicViewModel.getNewScreen(context = context)
                                }
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        }, onItemClick = { musicItem ->
                            if(musicItem.type == 0){
                                Log.d("LogNav", "TYPE = 0")
                                playerViewModel.resetPlaylist()
                                playerViewModel.setRadio(musicItem)
                                playerViewModel.playSong(musicItem, context, true)
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
                        ytmusicViewModel = ytmusicViewModel,
                        playerViewModel = playerViewModel,
                    )
                }
                composable("search") {
                    SearchScreen(
                        navController,
                        onTabSelected = { tabIndex ->
                            playerViewModel.setShouldExpand(-1);
                            when (tabIndex) {
                                0 -> navController.navigate("home")
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        onClick = { musicItem ->
                            if(musicItem.type == 0){
                                Log.d("LogNav", "TYPE = 0")
                                playerViewModel.resetPlaylist()
                                playerViewModel.playSong(musicItem, context, true)
                            } else if(musicItem.type == 1){
                                navController.currentBackStackEntry?.savedStateHandle?.set("title", musicItem.title)
                                navController.currentBackStackEntry?.savedStateHandle?.set("artist", musicItem.artist)
                                navController.currentBackStackEntry?.savedStateHandle?.set("image", musicItem.imageUrl)
                                Log.d("Playlists", musicItem.playlistId);
                                navController.navigate("playlist/${musicItem.playlistId}")
                                Log.d("LogNav", "TYPE = 1 with ${musicItem.playlistId}")
                            }
                            else if(musicItem.type == 2){
                                navController.navigate("album/${musicItem.browseId}")
                                Log.d("LogNav", "TYPE = 2")
                            }
                            else if(musicItem.type == 3){
                                navController.navigate("artist/${musicItem.browseId}")
                                Log.d("LogNav", "TYPE = 3")
                            }
                        },
                        searchViewModel = ytmusicViewModel
                    )
                }
                composable("library") { LibraryScreen(navController,
                    onItemClick = { it ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("title", it.title)
                        navController.currentBackStackEntry?.savedStateHandle?.set("artist", it.artist)
                        navController.currentBackStackEntry?.savedStateHandle?.set("image", it.imageUrl)
                        navController.navigate("playlist/${it.playlistId}")
                        Log.d("LogNav", "TYPE = 2")
                    },
                    ytmusicViewModel = ytmusicViewModel,
                    onTabSelected = { tabIndex ->
                        playerViewModel.setShouldExpand(-1);
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
                        ytmusicViewModel = ytmusicViewModel,
                        playlistItem = MusicItem(
                            videoId = "",
                            title = title,
                            artist = artist,
                            imageUrl = image,
                            type = 1,
                            playlistId = playlistId,
                        ),
                        onTabSelected = { tabIndex ->
                            playerViewModel.setShouldExpand(-1);
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        onSongClick = { musicItem, index, playlist ->
                            Log.d("LogNav", "TYPE = 0")
                            playerViewModel.setPlaylist(playlist)
                            playerViewModel.setIsShuffled(false)
                            playerViewModel.playSongInPlaylistAtIndex(index, context, true)
                        },
                        onShuffleClick = { playlist ->
                            playerViewModel.setPlaylist(playlist)
                            playerViewModel.shufflePlaylist()
                            playerViewModel.playSongInPlaylistAtIndex(0, context, true)
                        },
                        onPlayClick = { playlist ->
                            playerViewModel.setPlaylist(playlist)
                            playerViewModel.setIsShuffled(false)
                            playerViewModel.playSongInPlaylistAtIndex(0, context, true)
                        },
                        onPlayNextClick = { playlist ->
                            if (!playerViewModel.playlist.value.isNullOrEmpty())
                                playerViewModel.playlistInsertNext(playlist);
                            else {
                                playerViewModel.setPlaylist(playlist)
                                playerViewModel.setIsShuffled(false)
                                playerViewModel.playSongInPlaylistAtIndex(0, context, true)
                            }
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
                        ytMusicViewModel = ytmusicViewModel,
                        albumId = browseId,
                        onTabSelected = { tabIndex ->
                            playerViewModel.setShouldExpand(-1);
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        onSongClick = { musicItem, index, playlist ->
                            Log.d("LogNav", "TYPE = 0")
                            playerViewModel.setPlaylist(playlist)
                            playerViewModel.setIsShuffled(false)
                            playerViewModel.playSongInPlaylistAtIndex(index, context, true)
                        },
                        onShuffleClick = { playlist ->
                            playerViewModel.setPlaylist(playlist)
                            playerViewModel.shufflePlaylist()
                            playerViewModel.playSongInPlaylistAtIndex(0, context, true)
                        },
                        onPlayClick = { playlist ->
                            playerViewModel.setPlaylist(playlist)
                            playerViewModel.setIsShuffled(false)
                            playerViewModel.playSongInPlaylistAtIndex(0, context, true)
                        },
                        onPlayNextClick = { playlist ->
                            if (!playerViewModel.playlist.value.isNullOrEmpty())
                                playerViewModel.playlistInsertNext(playlist);
                            else {
                                playerViewModel.setPlaylist(playlist)
                                playerViewModel.setIsShuffled(false)
                                playerViewModel.playSongInPlaylistAtIndex(0, context, true)
                            }
                        },
                    )
                }
                composable(
                    route = "artist/{artistId}",
                    arguments = listOf(
                        navArgument("artistId"){type = NavType.StringType},
                    )
                ) {backStackEntry ->
                    val browseId = backStackEntry.arguments?.getString("artistId") ?: ""
                    ArtistPage(
                        BrowseID = browseId,
                        onClickMusicItem = { musicItem ->
                            if(musicItem.type == 0){
                                Log.d("LogNav", "TYPE = 0")
                                playerViewModel.resetPlaylist()
                                playerViewModel.playSong(musicItem, context, true)
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
                            else if(musicItem.type == 3){
                                navController.navigate("artist/${musicItem.browseId}")
                                Log.d("LogNav", "TYPE = 3")
                            }
                        },
                        onTabSelected = { tabIndex ->
                            playerViewModel.setShouldExpand(-1);
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        ytmusicViewModel = ytmusicViewModel,
                        navController = navController,
                    )
                }
                composable(
                    route = "mood/{params}",
                    arguments = listOf(
                        navArgument("params"){type = NavType.StringType}
                    ),
                ){backStackEntry ->
                    val params = backStackEntry.arguments?.getString("params") ?: ""
                    MoodListScreen(
                        params = params,
                        navController = navController,
                        onTabSelected = { tabIndex ->
                            playerViewModel.setShouldExpand(-1);
                            when (tabIndex) {
                                0 -> {navController.navigate("home")}
                                1 -> navController.navigate("new")
                                2 -> navController.navigate("library")
                                3 -> navController.navigate("search")
                            }
                        },
                        onPlaylistClick = { musicItem ->
                            if(musicItem.type == 0){
                                Log.d("LogNav", "TYPE = 0")
                                playerViewModel.resetPlaylist()
                                playerViewModel.playSong(musicItem, context, true)
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
                            else if(musicItem.type == 3){
                                navController.navigate("artist/${musicItem.browseId}")
                                Log.d("LogNav", "TYPE = 3")
                            }
                        },
                        ytmusicViewModel = ytmusicViewModel
                    )
                }
                composable(
                    route = "editPlaylist/{originalPlaylist}",
                    arguments = listOf(
                        navArgument("originalPlaylist") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val originalPlaylist = backStackEntry.arguments?.getString("originalPlaylist")

                    if (originalPlaylist != null) {
                        EditPlaylistScreen(
                            navController = navController,
                            originalPlaylist = originalPlaylist,
                            onDismiss = { navController.popBackStack() },
                            onPlaylistEdit = { updatedPlaylist ->
                                for(song in updatedPlaylist.songs){
                                    userViewModel.removeSongFromPlaylist(originalPlaylist, song.videoId)
                                }
                                userViewModel.editPlaylistTitle(originalPlaylist, updatedPlaylist.title)
                                navController.popBackStack()
                            },
                            viewModel = userViewModel
                        )
                    }
                }

            }
        }

        // Check if we're in any route within the main navigation
        if (currentBackStackEntry?.destination?.parent?.route == "main" && musicItem.videoId.isNotEmpty()) {
            NowPlayingSheet(
                playerViewModel = playerViewModel,
                visualizerViewModel = visualizerViewModel,
                navController = navController,
                ytmusicViewModel = ytmusicViewModel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            )
            Log.d("NavHost", "Overlay shown")
        }
    }
}
