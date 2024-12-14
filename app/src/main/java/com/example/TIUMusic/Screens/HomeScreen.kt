package com.example.TIUMusic.Screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.MusicDB.MusicViewModel
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.MusicItemType
import com.example.TIUMusic.Utils.nameToRID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

data class HomeTest(
    val title : String,
    val contents : List<MusicItem>,
)

@Composable
fun HomeScreen(
    navController: NavHostController,
    ytMusicViewModel: YtmusicViewModel,
    musicViewModel : MusicViewModel = MusicViewModel(LocalContext.current),
    userViewModel: UserViewModel = hiltViewModel(),
    onTabSelected: (Int) -> Unit = {},
    onItemClick: (MusicItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current;
    val homeItems by remember {
        mutableStateOf(
            listOf<HomeTest>(
//                HomeTest(
//                    contents =
//                        musicViewModel.getAlbums(context).map {
//                            MusicItem(
//                                title = it.title,
//                                artist = it.artist,
//                                videoId = "",
//                                type = MusicItemType.Album,
//                                imageUrl = it.imageUri,
//                                imageRId = nameToRID(it.imageUri, "raw", context),
//                                playlistId = it.id.toString(),
//                            )
//                        }
//                    ,
//                    title = "Hello"
//                ),
//                HomeTest(
//                    contents = musicViewModel.allSongs.subList(0, 10).map { it ->
//                            it.toMusicItem(context)
//                        },
//                    title = "Songs"
//                ),
//                HomeTest(
//                    contents = musicViewModel.playlist.map { it ->
//                        MusicItem(
//                            videoId = "",
//                            title = it.title,
//                            artist = it.artist,
//                            playlistId = it.id.toString(),
//                            type = MusicItemType.GlobalPlaylist,
//                            imageUrl = it.imageUri,
//                            playlistSongsIds = it.songsIds,
//                            imageRId = nameToRID(it.imageUri, "raw", context),
//                        )
//                    },
//                    title = "Playlists"
//                )
            )
        )
    }
    val mostListenedSongsIds by userViewModel.mostListenedSong.collectAsState()
    var mostListenedSong by remember { mutableStateOf(listOf<MusicItem>()) }
    val recentListenedSongIds by userViewModel.recentListenedSong.collectAsState()
    var recentListenedSong by remember { mutableStateOf(listOf<MusicItem>()) }

    LaunchedEffect(Unit) {
        userViewModel.getMostListenedSong();
        userViewModel.getRecentListenedSong();
    }

    LaunchedEffect(mostListenedSongsIds) {
        musicViewModel.getSongsWithIds(mostListenedSongsIds, context).collectLatest { songs ->
            mostListenedSong = mostListenedSongsIds.mapIndexed { index, id ->
                val found = songs.binarySearch { b ->
                    val bId = b.songId ?: 0;
                    if (id < bId) 1;
                    else if (id > bId) -1;
                    else 0;
                }
                songs[found];
            };
        };
        // Should it be combined or seperated?
    }

    LaunchedEffect(recentListenedSongIds) {
        musicViewModel.getSongsWithIds(recentListenedSongIds, context).collectLatest { songs ->
            recentListenedSong = recentListenedSongIds.mapIndexed { index, id ->
                val found = songs.binarySearch { b ->
                    val bId = b.songId ?: 0;
                    if (id < bId) 1;
                    else if (id > bId) -1;
                    else 0;
                }
                songs[found];
            };
        }
    }

    ScrollableScreen(
        title = "Home",
        selectedTab = 0,
        itemCount = homeItems.size,
        fetchContinuation = {

        },
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            if (mostListenedSong.isNotEmpty()) {
                run {
                    val homeTest = HomeTest(
                        contents = mostListenedSong,
                        title = "Most listened"
                    )
                    HorizontalScrollableSection(
                        title = homeTest.title,
                        items = homeTest.contents,
                        itemWidth = 200.dp,
                        sectionHeight = 280.dp,
                        onItemClick = onItemClick
                    );
                }
            }

            if (recentListenedSong.isNotEmpty()) {
                run {
                    val homeTest = HomeTest(
                        contents = recentListenedSong,
                        title = "Recently"
                    )
                    HorizontalScrollableSection(
                        title = homeTest.title,
                        items = homeTest.contents,
                        itemWidth = 200.dp,
                        sectionHeight = 280.dp,
                        onItemClick = onItemClick
                    );
                }
            }
        }
    }
}

