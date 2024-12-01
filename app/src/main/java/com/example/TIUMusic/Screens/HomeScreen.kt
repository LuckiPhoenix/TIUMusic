package com.example.TIUMusic.Screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Libs.YoutubeLib.getYoutubeHDThumbnail
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeContent
import com.example.TIUMusic.SongData.MusicItem

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
        ytMusicViewModel.getContinuation(context);
    }

    ScrollableScreen(
        title = "Home",
        selectedTab = 0,
        itemCount = homeItems.size,
        fetchContinuation = {
            ytMusicViewModel.getContinuation(context);
        },
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
            musicItems.add(fromHomeContent(item, item.videoId != null));
    }
    return musicItems;
}

private fun fromHomeContent(item : HomeContent, useHDImage: Boolean) : MusicItem {
    var type = 0
    var id = ""
    if(item.browseId != null){
        type = 2
        id = item.browseId
    }
    if(item.playlistId != null){
        type = 1
        id = item.playlistId
    }
    if(item.videoId != null){
        type = 0
        id = item.videoId
    }
    if (useHDImage) {
        val hdImageUrl = if (item.videoId != null) getYoutubeHDThumbnail(item.videoId) else "";
        return MusicItem(
            videoId = item.videoId ?: "",
            title = item.title,
            artist = item.artists?.firstOrNull()?.name ?: "",
            imageUrl = hdImageUrl,
            type = 0,
        )
    }
    return MusicItem(
        videoId = id,
        title = item.title,
        artist = item.artists?.firstOrNull()?.name ?: "",
        imageUrl = item.thumbnails.lastOrNull()?.url ?: "",
        type = type
    )
}