package com.example.TIUMusic.Screens

import android.content.ClipData.Item
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.TIUMusic.Login.reusableInputField
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.ui.theme.ArtistNameColor
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.ButtonColor
import com.example.TIUMusic.ui.theme.PrimaryColor
import com.example.TIUMusic.ui.theme.SecondaryColor


@Composable
fun AlbumListScreen(navController: NavController, playlistId: String) {
    Scaffold(
        topBar = { TopPlaylistBar("Favourite", navController) },
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 2,
                onTabSelected = {},
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
                        model = "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png",
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF282828))
                            .padding(4.dp)
                    )
                    Text(text = "Submarine - EP", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White, modifier = Modifier.padding(4.dp))
                    Text(text = "Alex Turner", fontSize = 20.sp, color = PrimaryColor, modifier = Modifier.padding(4.dp))
                    Text(text = "Alternative · 2011", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ArtistNameColor, modifier = Modifier.padding(4.dp))
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
            items(SongListSample()) { item ->
                SongInAlbum(item.title, item.artist, item.imageUrl)
                HorizontalDivider(
                    thickness = 2.dp,
                    color = ButtonColor,
                    modifier = Modifier.padding(start = 34.dp, end = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SongInAlbum(title: String, artist: String, albumCover: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
            .padding(start = 8.dp, bottom = 12.dp, top = 12.dp, end = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "1",
                fontSize = 20.sp,
                color = ArtistNameColor,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp)
            )
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp
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

@Preview
@Composable
fun preview3 () {
    val navController = rememberNavController()
    AlbumListScreen(navController, "8901")
}