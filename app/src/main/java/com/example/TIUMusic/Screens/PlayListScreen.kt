package com.example.TIUMusic.Screens

import android.content.ClipData.Item
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Login.reusableInputField
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.ui.theme.ArtistNameColor
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.ButtonColor
import com.example.TIUMusic.ui.theme.PrimaryColor
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel.UiState
import com.example.TIUMusic.ui.theme.SecondaryColor

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
                painter = painterResource(R.drawable.arrow_down_button),
                contentDescription = "Return Button",
                modifier = Modifier.padding(16.dp),
                tint = PrimaryColor
            )
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
    onSongClick: (MusicItem) -> Unit = {},
    ytMusicViewModel: YtmusicViewModel = hiltViewModel(),) {
    val playlistState by ytMusicViewModel.listTrackItems.collectAsState()

    LaunchedEffect(Unit) {
        ytMusicViewModel.SongListSample(playlistItem.playlistId)
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
                CircularProgressIndicator()
                Log.d("LogNav", "Loading")
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
                                model = playlistItem.imageUrl,
                                contentDescription = "Album Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF282828))
                            )
                            Text(text = playlistItem.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White, modifier = Modifier.padding(4.dp))
                            Text(text = playlistItem.artist, fontSize = 16.sp, color = PrimaryColor, modifier = Modifier.padding(4.dp))
                            Text(text = playlistItem.artist, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray, modifier = Modifier.padding(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Card(
                                    modifier = Modifier
                                        .size(160.dp, 52.dp)
                                        .padding(4.dp),
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
                                        .padding(4.dp),
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
                    items(state.data){item ->
                        SongInPlaylist(
                            item,
                            onClick = {onSongClick(item)} )
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = ButtonColor,
                            modifier = Modifier.padding(start = 66.dp, end = 8.dp)
                        )
                    }
                }
            }
            is UiState.Error -> {

            }
        }

    }
}


@Composable
fun SongInPlaylist(item: MusicItem, onClick: () -> Unit = {}) {
    val title = item.title
    val albumCover = item.imageUrl
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
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = title,
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
        Icon(
            painter = painterResource(R.drawable.ellipsis_vertical_button),
            contentDescription = "Option Button",
            modifier = Modifier
                .padding(4.dp)
                .size(16.dp),
            tint = Color.White,
        )
    }
}
