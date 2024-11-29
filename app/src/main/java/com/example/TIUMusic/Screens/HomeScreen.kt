package com.example.TIUMusic.Screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeContent
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeItem
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.getBasedOnRecent
import com.example.TIUMusic.SongData.getPlaylists
import com.example.TIUMusic.SongData.getRecentItems
import com.example.TIUMusic.SongData.getTopPicks

@Composable
fun HomeScreen(
    navController: NavHostController,
    ytMusicViewModel: YtmusicViewModel = hiltViewModel(),
    onTabSelected: (Int) -> Unit = {},
    onPlaylistClick: (MusicItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current;
    var homeItems : List<HomeItem> by remember { mutableStateOf(emptyList()) }
    LaunchedEffect(Unit) {
        homeItems = ytMusicViewModel.getHomeScreen(context);
    }

    ScrollableScreen(
        title = "Home",
        selectedTab = 0,
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            homeItems.forEach {
                HorizontalScrollableSection(
                    title = it.title,
                    items = toHomeContentsList(it.contents),
                    itemWidth = 200.dp,
                    sectionHeight = 280.dp,
                    onItemClick = onPlaylistClick
                );
            }
//            HorizontalScrollableSection(
//                title = "Top picks for you",
//                items = getTopPicks(),
//                itemWidth = 200.dp,
//                sectionHeight = 280.dp,
//                onItemClick = onPlaylistClick
//            )
//
//            HorizontalScrollableSection(
//                title = "Recent",
//                items = getRecentItems(),
//                onItemClick = onPlaylistClick
//            )
//
//            HorizontalScrollableSection(
//                title = "Based on recent activity",
//                items = getBasedOnRecent(),
//                onItemClick = onPlaylistClick
//            )
//
//            HorizontalScrollableSection(
//                title = "Made for you",
//                items = getPlaylists(),
//                onItemClick = onPlaylistClick
//            )
        }
    }
}

private fun toHomeContentsList(list : List<HomeContent?>) : List<MusicItem> {
    val musicItems = mutableListOf<MusicItem>();
    for (item in list) {
        if (item != null)
            musicItems.add(fromHomeContent(item));
    }
    return musicItems;
}

private fun fromHomeContent(item : HomeContent) : MusicItem {
    return MusicItem(
        id = item.videoId,
        title = item.title,
        artist = item.artists?.firstOrNull()?.name,
        imageUrl = item.thumbnails.firstOrNull()?.url
    )
}