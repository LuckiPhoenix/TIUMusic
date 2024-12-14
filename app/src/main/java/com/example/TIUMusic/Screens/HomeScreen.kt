package com.example.TIUMusic.Screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

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
    var homeItems by remember { mutableStateOf(listOf<HomeTest>()) }
    val mostListenedSongsIds by userViewModel.mostListenedSong.collectAsState()
    val recentListenedSongIds by userViewModel.recentListenedSong.collectAsState()
    var continuation by remember { mutableStateOf(false) }
    val fetchContinuationFunc = suspend {
        val choose = Random.nextInt(1, 4);
        println("fetching $choose");
        when (choose) {
            1 -> {
                musicViewModel.getRandomAlbums(5, context).collectLatest {
                    continuation = false;
                    if (it.isEmpty())
                        return@collectLatest;
                    val items = homeItems.toMutableList();
                    items.add(
                        HomeTest(
                            contents = it,
                            title = "Albums",
                        )
                    )
                    homeItems = items;
                };
            }

            2 -> {
                musicViewModel.getRandomPlaylist(5, context).collectLatest {
                    continuation = false;
                    if (it.isEmpty())
                        return@collectLatest;
                    val items = homeItems.toMutableList();
                    items.add(
                        HomeTest(
                            contents = it,
                            title = "Playlists",
                        )
                    )
                    homeItems = items;
                };
            }

            3 -> {
                musicViewModel.getRandomSongs(10, context).collectLatest {
                    continuation = false;
                    if (it.isEmpty())
                        return@collectLatest;
                    val items = homeItems.toMutableList();
                    items.add(
                        HomeTest(
                            contents = it,
                            title = "Songs",
                        )
                    )
                    homeItems = items;
                };
            }
        }
    }

    LaunchedEffect(continuation) {
        if (continuation) {
            delay(2000);
            repeat(3) {
                fetchContinuationFunc();
            }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.getMostListenedSong();
        userViewModel.getRecentListenedSong();
        delay(1000);
        repeat(3) {
            fetchContinuationFunc();
        }
    }

    LaunchedEffect(mostListenedSongsIds) {
        musicViewModel.getSongsWithIds(mostListenedSongsIds, context).collectLatest { songs ->
            val mostListenedSong = mostListenedSongsIds.mapIndexed { index, id ->
                val found = songs.binarySearch { b ->
                    val bId = b.songId ?: 0;
                    if (id < bId) 1;
                    else if (id > bId) -1;
                    else 0;
                }
                songs[found];
            };
            if (mostListenedSong.isNotEmpty()) {
                val items = homeItems.toMutableList();
                items.add(
                    HomeTest(
                        title = "Most Listened",
                        contents = mostListenedSong
                    )
                )
                homeItems = items;
            }
        };
        // Should it be combined or seperated?
    }

    LaunchedEffect(recentListenedSongIds) {
        musicViewModel.getSongsWithIds(recentListenedSongIds, context).collectLatest { songs ->
            val recentListenedSong = recentListenedSongIds.mapIndexed { index, id ->
                val found = songs.binarySearch { b ->
                    val bId = b.songId ?: 0;
                    if (id < bId) 1;
                    else if (id > bId) -1;
                    else 0;
                }
                songs[found];
            };
            if (recentListenedSong.isNotEmpty()) {
                val items = homeItems.toMutableList();
                items.add(
                    HomeTest(
                        title = "Recently",
                        contents = recentListenedSong
                    )
                )
                homeItems = items;
            }
        }
    }

    ScrollableScreen(
        title = "Home",
        selectedTab = 0,
        itemCount = homeItems.size,
        fetchContinuation = {
            continuation = true;
        },
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            homeItems.forEach {
                HorizontalScrollableSection(
                    title = it.title,
                    items = it.contents,
                    itemWidth = 200.dp,
                    sectionHeight = 280.dp,
                    onItemClick = onItemClick
                );
            }
        }
    }
}

