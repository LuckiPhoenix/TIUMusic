package com.example.TIUMusic.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.ui.theme.BackgroundColor
import kotlinx.coroutines.launch

@Composable
fun ArtistPage(
    BrowseID: String,
    onClickMusicItem: (MusicItem) -> Unit = {},
    onClickAlbum: (MusicItem) -> Unit = {},
    onTabSelected: (Int) -> Unit,
    ytmusicViewModel: YtmusicViewModel,
    navController: NavController
) {
    val artistImageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg" // Mock image URL
    val trendingItem by ytmusicViewModel.chart.collectAsState()

    LaunchedEffect(Unit) {
        ytmusicViewModel.getChart("US")
    }

    // Use LazyColumn for proper scrolling behavior
    Box(
        modifier = Modifier.background(BackgroundColor)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    // Artist Image
                    AsyncImage(
                        model = artistImageUrl,
                        contentDescription = "Artist Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = "BrowseID", //artist name here
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }
            }

            // Trending Songs Section
            trendingItem?.songsToMusicItem(3)?.let { trendingSongList ->
                if (trendingSongList.isNotEmpty()) {
                    item {
                        HorizontalScrollableNewScreenSection2(
                            title = "Trending Songs",
                            iconHeader = R.drawable.baseline_chevron_right_24,
                            items = trendingSongList,
                            itemWidth = 300.dp,
                            sectionHeight = 260.dp,
                            onItemClick = onClickMusicItem
                        )
                    }
                }
            }

            trendingItem?.videoPlaylist?.videosToMusicItems()?.let { topMusicVideos ->
                if (topMusicVideos.isNotEmpty()) {
                    item {
                        HorizontalScrollableNewScreenSection3(
                            title = "Top Music Videos",
                            iconHeader = R.drawable.baseline_chevron_right_24,
                            items = topMusicVideos,
                            itemWidth = 150.dp,
                            sectionHeight = 220.dp,
                            onItemClick = onClickAlbum
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(160.dp)) }

        }
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left_buttom),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        CustomBottomNavigation(
            selectedTab = 2,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}