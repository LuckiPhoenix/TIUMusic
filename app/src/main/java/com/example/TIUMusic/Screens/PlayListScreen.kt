package com.example.TIUMusic.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.ui.theme.ArtistNameColor
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.ButtonColor
import com.example.TIUMusic.ui.theme.PrimaryColor
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel.UiState
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.SongData.MoodItem
import com.example.TIUMusic.SongData.getTopPicks

@Composable
fun TopPlaylistBar(
    title: String,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .padding(
                start = 8.dp,
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding()
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_left_buttom),
            contentDescription = "Return Button",
            modifier = Modifier
                .padding(16.dp)
                .clickable { navController.popBackStack() },
            tint = PrimaryColor
        )
        Row(
        ) {
            Icon(
                painter = painterResource(R.drawable.ellipsis_vertical_button),
                contentDescription = "Return Button",
                modifier = Modifier.padding(16.dp),
                tint = PrimaryColor
            )
        }
    }
}


@Composable
fun PlaylistScreen(
    navController: NavController,
    playlistItem: MusicItem,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSongClick: (MusicItem, Int, List<MusicItem>) -> Unit,
    onShuffleClick : (List<MusicItem>) -> Unit,
    onPlayClick: (List<MusicItem>) -> Unit,
    ytmusicViewModel: YtmusicViewModel,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val playlistState by ytmusicViewModel.listTrackItems.collectAsState()
    val localList by userViewModel.playlist.observeAsState()

    LaunchedEffect(Unit) {
        ytmusicViewModel.SongListSample(playlistItem.playlistId)
        userViewModel.getPlaylistById(playlistItem.playlistId)
    }
    Scaffold(
        topBar = { TopPlaylistBar("Favourite", navController) },
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 2,
                onTabSelected = onTabSelected,
                modifier = Modifier
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        when (val state = playlistState){
            is UiState.Initial -> {
                // Trạng thái ban đầu
                Log.d("LogNav", "Initial id : ${playlistItem.playlistId}")
            }
            is UiState.Loading -> {
                LoadingScreen()
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(start = 8.dp, end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        // Header content
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AsyncImage(
                                model = playlistItem.imageUrl,
                                contentDescription = "Album Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF282828))
                            )
                            Text(
                                text = playlistItem.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White,
                                modifier = Modifier.padding(6.dp),
                                textAlign = TextAlign.Center
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Card(
                                    modifier = Modifier
                                        .size(160.dp, 52.dp)
                                        .padding(4.dp)
                                        .clickable {
                                            if(state.data.isEmpty() && ytmusicViewModel.isPlaylistRandomUUID(playlistItem.playlistId))
                                                onPlayClick(localList?.songs ?: emptyList());
                                            else
                                                onPlayClick(state.data);
                                        },
                                    colors = CardColors(ButtonColor, PrimaryColor, Color.Gray, Color.Black)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.play_solid),
                                            contentDescription = "Play Button",
                                            modifier = Modifier.padding(12.dp),
                                            tint = PrimaryColor
                                        )
                                        Text(
                                            text = "Play",
                                            fontSize = 18.sp,
                                            color = PrimaryColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .size(160.dp, 52.dp)
                                        .padding(4.dp)
                                        .clickable {
                                            if(state.data.isEmpty() && ytmusicViewModel.isPlaylistRandomUUID(playlistItem.playlistId))
                                                onShuffleClick(localList?.songs ?: emptyList())
                                            else
                                                onShuffleClick(state.data);
                                        },
                                    colors = CardColors(ButtonColor, PrimaryColor, Color.Gray, Color.Black)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.shuffle_button),
                                            contentDescription = "Play Button",
                                            modifier = Modifier.padding(12.dp),
                                            tint = PrimaryColor
                                        )
                                        Text(
                                            text = "Shuffle",
                                            fontSize = 18.sp,
                                            color = PrimaryColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(
                                thickness = 2.dp,
                                color = ButtonColor,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp)
                            )
                        }
                    }

                    // Song list
                    val listSong: List<MusicItem>
                    if(state.data.isEmpty() && ytmusicViewModel.isPlaylistRandomUUID(playlistItem.playlistId)){
                        listSong = localList?.songs
                            ?: emptyList()
                    }
                    else{
                        listSong = state.data
                    }

                    itemsIndexed(listSong){ index, item ->
                        SongInPlaylist(
                            item,
                            onClick = { onSongClick(item, index, listSong) }
                        )
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = ButtonColor,
                            modifier = Modifier.padding(start = 66.dp, end = 8.dp)
                        )
                    }

                    item { Spacer(modifier = Modifier.height(88.dp)) }
                }
            }
            is UiState.Error -> {

            }
        }

    }
}

@Composable
fun AlbumScreen(
    navController: NavController,
    albumId: String,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSongClick: (MusicItem, Int, List<MusicItem>) -> Unit,
    onPlayClick: (List<MusicItem>) -> Unit,
    onShuffleClick: (List<MusicItem>) -> Unit,
    ytMusicViewModel: YtmusicViewModel,
) {
    val albumState by ytMusicViewModel.albumPage.collectAsState()

    LaunchedEffect(Unit) {
        ytMusicViewModel.fetchAlbumSongs(albumId)
    }

    Scaffold(
        topBar = { TopPlaylistBar("Album", navController) },
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 2,
                onTabSelected = onTabSelected,
                modifier = Modifier
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        when (val state = albumState){
            is UiState.Initial -> {
                // Trạng thái ban đầu
                Log.d("LogNav", "Initial id : ${albumId}")
            }
            is UiState.Loading -> {
                LoadingScreen()
            }
            is UiState.Success -> {
                Log.d("LogNav", "Success")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(start = 8.dp, end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        // Header content
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AsyncImage(
                                model = state.data.imageUrl,
                                contentDescription = "Album Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF282828))
                            )
                            Text(
                                text = state.data.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp, color = Color.White,
                                modifier = Modifier.padding(4.dp),
                                textAlign = TextAlign.Center)
                            Text(
                                text = state.data.artist,
                                fontSize = 16.sp,
                                color = PrimaryColor,
                                modifier = Modifier.padding(4.dp)
                            )
                            state.data.description?.let {  }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Card(
                                    modifier = Modifier
                                        .size(160.dp, 52.dp)
                                        .padding(4.dp)
                                        .clickable {
                                            onPlayClick(state.data.songs)
                                        },
                                    colors = CardColors(ButtonColor, PrimaryColor, Color.Gray, Color.Black)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.play_solid),
                                            contentDescription = "Play Button",
                                            modifier = Modifier.padding(12.dp),
                                            tint = PrimaryColor
                                        )
                                        Text(
                                            text = "Play",
                                            fontSize = 18.sp,
                                            color = PrimaryColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .size(160.dp, 52.dp)
                                        .padding(4.dp)
                                        .clickable {
                                            onShuffleClick(state.data.songs)
                                        },
                                    colors = CardColors(ButtonColor, PrimaryColor, Color.Gray, Color.Black)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.shuffle_button),
                                            contentDescription = "Play Button",
                                            modifier = Modifier.padding(12.dp),
                                            tint = PrimaryColor
                                        )
                                        Text(
                                            text = "Shuffle",
                                            fontSize = 18.sp,
                                            color = PrimaryColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(
                                thickness = 2.dp,
                                color = ButtonColor,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp)
                            )
                        }
                    }
                    // Song list
                    itemsIndexed(state.data.songs){ index, item ->
                        SongInAlbumlist(
                            item,
                            index,
                            onClick = { onSongClick(item, index, state.data.songs) }
                        )
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = ButtonColor,
                            modifier = Modifier.padding(start = 66.dp, end = 8.dp)
                        )
                    }
                    item { Spacer(modifier = Modifier.height(88.dp)) }
                }
            }
            is UiState.Error -> {

            }
        }

    }
}

@Composable
fun MoodListScreen(
    params: String,
    navController: NavController,
    onTabSelected: (Int) -> Unit,
    onPlaylistClick: (MusicItem) -> Unit,
    ytmusicViewModel: YtmusicViewModel
) {
    val listMusicItem by ytmusicViewModel.moodfetch.collectAsState()

    LaunchedEffect(Unit) {
        ytmusicViewModel.fetchMoodItem(params = params)
    }
    Scaffold(
        topBar = {
            TopPlaylistBar(listMusicItem.first, navController)
            Text(
                text = listMusicItem.first,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .padding(PaddingValues(top = 90.dp, start = 30.dp, bottom = 10.dp)),
                textAlign = TextAlign.Center,
            )
        },
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 2,
                onTabSelected = onTabSelected,
                modifier = Modifier
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .height(600.dp), // Adjust height as needed
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item{}
            item{}
            items(listMusicItem.second) { item ->
                AlbumCard(
                    item = item,
                    modifier = Modifier,
                    imageSize = 180.dp,
                    onClick = {
                        onPlaylistClick(item)
                    }
                )
            }
            item {Spacer(modifier = Modifier.height(88.dp))}
        }
    }
}


@Composable
fun SongInPlaylist(item: MusicItem, onClick: () -> Unit = {}) {
    val title = item.title
    val albumCover = item.getSmallThumbnail()
    val artist = item.artist
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 2.dp, top = 6.dp, end = 4.dp)
            .clickable {
                onClick()
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = albumCover,
                contentDescription = "Song Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF282828))
            )



            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(70.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                )
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
                Text(
                    text = artist,
                    color = ArtistNameColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SongInAlbumlist(item: MusicItem, index: Int, onClick: () -> Unit = {}) {
    val title = item.title
    val albumCover = item.getSmallThumbnail()
    val artist = item.artist
    val index = index + 1
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 2.dp, top = 6.dp, end = 4.dp)
            .clickable {
                onClick()
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = index.toString(),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
            )
            AsyncImage(
                model = albumCover,
                contentDescription = "Song Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF282828))
            )



            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(70.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                )
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
                Text(
                    text = artist,
                    color = ArtistNameColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}