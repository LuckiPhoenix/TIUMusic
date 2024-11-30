package com.example.TIUMusic.Screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(
    navController: NavHostController,
    ytMusicViewModel: YtmusicViewModel = hiltViewModel(),
    onTabSelected: (Int) -> Unit = {},
    onPlaylistClick: (MusicItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current;
    val homeItems by ytMusicViewModel.homeItems.collectAsState(emptyList());

    LaunchedEffect(Unit) {
        ytMusicViewModel.GetContinuation(context);
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
        id = item.videoId ?: "",
        title = item.title,
        artist = item.artists?.firstOrNull()?.name ?: "",
        imageUrl = item.thumbnails.lastOrNull()?.url ?: ""
    )
}