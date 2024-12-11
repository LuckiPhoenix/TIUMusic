package com.example.TIUMusic.Screens

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.toBitmap
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel.UiState
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.ButtonColor
import com.example.TIUMusic.ui.theme.PrimaryColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun TopPersonalPlaylistBar(
    title: String,
    navController: NavController,
    onMenuClick: () -> Unit
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
                painter = painterResource(R.drawable.magnifying_glass_solid),
                contentDescription = "Search Button",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {

                    }
                ,
                tint = PrimaryColor
            )
            Icon(
                painter = painterResource(R.drawable.ellipsis_vertical_button),
                contentDescription = "Menu button",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        onMenuClick()
                    }
                ,
                tint = PrimaryColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalPlaylistMenuBottomSheet(
    navController: NavController,
    musicItem: MusicItem,
    playerViewModel: PlayerViewModel,
    onDismiss: () -> Unit
) {
    val (showDeleteConfirmation, setShowDeleteConfirmation) = remember { mutableStateOf(false) }
    val (showSortByBottomSheet, setShowSortByBottomSheet) = remember { mutableStateOf(false) }

    ModalBottomSheet(
        containerColor = Color.Black,
        shape = RoundedCornerShape(0.dp),
        onDismissRequest = {
            if (showDeleteConfirmation || showSortByBottomSheet) null else onDismiss()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = musicItem.imageUrl,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )
            Text(
                text = "My Playlist", //Khi nao tao chuc nang thi sua thanh musicItem.title
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        HorizontalDivider(
            thickness = 2.dp,
            color = Color(color = 0xFF878787),
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    setShowSortByBottomSheet(true)
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_down_up),
                contentDescription = "Sort button",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Sort By"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.pen_to_square_solid),
                contentDescription = "Edit playlist",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Edit Playlist",
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    setShowDeleteConfirmation(true)
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.trash_can_solid),
                contentDescription = "Delete Button",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Delete from Library"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.list_plus),
                contentDescription = "Play Next",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Play Next",
                color = Color.White
            )
        }
        if (showDeleteConfirmation) {
            AlertDialog(
                containerColor = Color.Black,
                shape = RoundedCornerShape(5.dp),
                onDismissRequest = { setShowDeleteConfirmation(false) },
                title = {
                    Text(text = "Confirm Delete")
                },
                text = {
                    Text(text = "Are you sure you want to delete ${musicItem.title} from your library?")
                },
                confirmButton = {
                    TextButton(onClick = { /* Delete logic */ setShowDeleteConfirmation(false) }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { setShowDeleteConfirmation(false) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showSortByBottomSheet) {
            ModalBottomSheet (
                containerColor = Color.Black,
                shape = RoundedCornerShape(0.dp),
                onDismissRequest = { setShowSortByBottomSheet(false) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Title of the Bottom Sheet
                    Text(
                        text = "Sort By",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Sort Options
                    val options = listOf("Playlist Order", "Title", "Artist", "Release Date")
                    val selectedOption = remember { mutableStateOf("Release Date") } // Default selection

                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedOption.value = option // Update the selected option
                                    setShowSortByBottomSheet(false) // Close the bottom sheet
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                            if (selectedOption.value == option) {
                                Icon(
                                    painter = painterResource(R.drawable.check_solid),
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalPlaylistScreen(
    navController: NavController,
    playlistItem: MusicItem,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSongClick: (MusicItem, Int, List<MusicItem>) -> Unit,
    onShuffleClick : (List<MusicItem>) -> Unit,
    onPlayClick: (List<MusicItem>) -> Unit,
    ytmusicViewModel: YtmusicViewModel,
    playerViewModel: PlayerViewModel
) {
    var showPersonalPlaylistMenu by remember { mutableStateOf(false) }
    val playlistState by ytmusicViewModel.listTrackItems.collectAsState()

    LaunchedEffect(Unit) {
        ytmusicViewModel.SongListSample(playlistItem.playlistId)
    }
    Scaffold(
        topBar = { TopPersonalPlaylistBar(
            "Favourite",
            navController,
            onMenuClick = { showPersonalPlaylistMenu = !showPersonalPlaylistMenu }
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

            item { Spacer(modifier = Modifier.height(88.dp)) }
        }
        if(showPersonalPlaylistMenu == true){
            PersonalPlaylistMenuBottomSheet(
                navController,
                musicItem = playlistItem,
                playerViewModel = playerViewModel,
                onDismiss = { showPersonalPlaylistMenu = false }
            )
        }
    }
}