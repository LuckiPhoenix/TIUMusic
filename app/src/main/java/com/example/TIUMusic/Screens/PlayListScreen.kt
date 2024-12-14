package com.example.TIUMusic.Screens

import android.util.Log
import androidx.compose.foundation.Image
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
import com.example.TIUMusic.MusicDB.MusicViewModel
import com.example.TIUMusic.SongData.MoodItem
import com.example.TIUMusic.SongData.MusicItemType
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.Utils.isPlaylistRandomUUID

@Composable
fun TopPlaylistBar(
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
                painter = painterResource(R.drawable.ellipsis_vertical_button),
                contentDescription = "Return Button",
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
fun PlaylistMenuBottomSheet(
    navController: NavController,
    musicItem: MusicItem,
    onPlayNextClick : () -> Unit,
    onSortByClick: (String) -> Unit,
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
                text = "My Playlist",
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
                modifier = Modifier.size(32.dp)
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
                    onPlayNextClick();
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
                text = "Play Next"
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
                                    onSortByClick(selectedOption.value)
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
fun PlaylistScreen(
    navController: NavController,
    playlistItem: MusicItem,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    musicViewModel: MusicViewModel = MusicViewModel(LocalContext.current),
    onSongClick: (MusicItem, Int, List<MusicItem>) -> Unit,
    onShuffleClick : (List<MusicItem>) -> Unit,
    onPlayClick: (List<MusicItem>) -> Unit,
    onPlayNextClick: (List<MusicItem>) -> Unit,
    ytmusicViewModel: YtmusicViewModel,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current;
    var showPersonalPlaylistMenu by remember { mutableStateOf(false) }
    var currentPlaylist by remember { mutableStateOf(listOf<MusicItem>()) }

    LaunchedEffect(Unit) {
        if (isPlaylistRandomUUID(playlistItem.playlistId)) {
            ytmusicViewModel.SongListSample(playlistItem.playlistId);
            TODO();
        }
        else if (playlistItem.type == MusicItemType.Album) {
            currentPlaylist = musicViewModel.getSongsInAlbum(playlistItem.playlistId.toInt(), context);
        }
        else if (playlistItem.type == MusicItemType.GlobalPlaylist) {
            currentPlaylist = musicViewModel.getSongsWithIds(playlistItem.playlistSongsIds, context);
        }
    }

    Scaffold(
        topBar = {
            TopPlaylistBar(
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
                    Image(
                        painter = painterResource(playlistItem.imageRId ?: R.drawable.tiumarksvg),
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
                                    onPlayClick(currentPlaylist);
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
                                    onShuffleClick(currentPlaylist);
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

            itemsIndexed(currentPlaylist){ index, item ->
                SongInPlaylist(
                    item,
                    onClick = { onSongClick(item, index, currentPlaylist) }
                )
                HorizontalDivider(
                    thickness = 2.dp,
                    color = ButtonColor,
                    modifier = Modifier.padding(start = 66.dp, end = 8.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(88.dp)) }
        }
        if(showPersonalPlaylistMenu == true){
            PlaylistMenuBottomSheet(
                navController,
                musicItem = playlistItem,
                onDismiss = { showPersonalPlaylistMenu = false },
                onPlayNextClick = {
                    onPlayNextClick(currentPlaylist);
                    showPersonalPlaylistMenu = false;
                },
                onSortByClick = { option ->
                    val tempPlaylist = currentPlaylist.toMutableList();
                    when (option) {
                        "Title" -> tempPlaylist.sortBy { it.title };
                        "Artist" -> tempPlaylist.sortBy { it.artist };
                        else -> {}
                    }
                    currentPlaylist = tempPlaylist;
                }
            )
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
    onPlayNextClick: (List<MusicItem>) -> Unit,
    onShuffleClick: (List<MusicItem>) -> Unit,
    ytMusicViewModel: YtmusicViewModel,
) {
    var showPersonalPlaylistMenu by remember { mutableStateOf(false) }
    val albumState by ytMusicViewModel.albumPage.collectAsState()
    var currentAlbum by remember { mutableStateOf(listOf<MusicItem>()) }

    LaunchedEffect(Unit) {
        ytMusicViewModel.fetchAlbumSongs(albumId)
    }


    LaunchedEffect(albumState) {
        when (val state = albumState) {
            is UiState.Initial -> {
                // Trạng thái ban đầu
                Log.d("LogNav", "Initial id : ${albumId}")
            }
            is UiState.Success -> {
                currentAlbum = state.data.songs;
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = { TopPlaylistBar(
            "Album",
            navController,
            onMenuClick = { showPersonalPlaylistMenu = !showPersonalPlaylistMenu }
        ) },
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
                    itemsIndexed(currentAlbum){ index, item ->
                        SongInAlbumlist(
                            item,
                            index,
                            onClick = { onSongClick(item, index, currentAlbum) }
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
        if(showPersonalPlaylistMenu == true){
            PlaylistMenuBottomSheet(
                navController,
                musicItem = MusicItem("", "", "", "", MusicItemType.Song),
                onDismiss = { showPersonalPlaylistMenu = false },
                onPlayNextClick = {
                    onPlayNextClick(currentAlbum);
                    showPersonalPlaylistMenu = false;
                },
                onSortByClick = {  option ->
                    val tempPlaylist = currentAlbum.toMutableList();
                    when (option) {
                        "Title" -> tempPlaylist.sortBy { it.title };
                        "Artist" -> tempPlaylist.sortBy { it.artist };
                        else -> {}
                    }
                    currentAlbum = tempPlaylist;
                }
            )
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
    var showPersonalPlaylistMenu by remember { mutableStateOf(false) }
    val listMusicItem by ytmusicViewModel.moodfetch.collectAsState()

    LaunchedEffect(Unit) {
        ytmusicViewModel.fetchMoodItem(params = params)
    }
    Scaffold(
        topBar = {
            TopPlaylistBar(
                listMusicItem.first,
                navController,
                onMenuClick = { showPersonalPlaylistMenu = !showPersonalPlaylistMenu }
            )
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
    val albumCover = item.imageRId
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
            Image(
                painter = painterResource(albumCover ?: R.drawable.tiumarksvg),
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