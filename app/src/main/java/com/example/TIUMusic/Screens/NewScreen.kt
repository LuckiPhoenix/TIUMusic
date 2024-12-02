package com.example.TIUMusic.Screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeMetadata
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.NewReleaseCard
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.SongData.fromHomeContent
import kotlinx.coroutines.launch


@Composable
fun NewScreen(
    navController: NavController,
    onTabSelected: (Int) -> Unit,
    onPlaylistClick: (MusicItem) -> Unit,
    ytmusicViewModel: YtmusicViewModel,
    playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current;
    val trendingItem by ytmusicViewModel.chart.collectAsState();
    val newReleases by ytmusicViewModel.newReleases.collectAsState();

    LaunchedEffect(Unit) {
        launch {
            ytmusicViewModel.getChart("US");
        }
        launch {
            ytmusicViewModel.getNewReleases(context);
        }
    }

    ScrollableScreen(
        title = "New",
        selectedTab = 1,
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            val newReleaseMusicItems = mutableListOf<NewReleaseCard>();
            for (newRelease in newReleases) {
                var i = 0;
                newRelease.contents.forEach {
                    if (i >= 3)
                        return@forEach;
                    if (it != null) {
                        newReleaseMusicItems.add(
                            NewReleaseCard(
                                newRelease.title,
                                fromHomeContent(it, false),
                            )
                        )
                    }
                    i++;
                }
            }
            if (newReleaseMusicItems.isNotEmpty()){
                HorizontalScrollableNewScreenSection(
                    items = newReleaseMusicItems,
                    itemWidth = 300.dp,
                    sectionHeight = 300.dp,
                    onItemClick = { }
                )
            }

            val trendingSongList = trendingItem?.songsToMusicItem(3);
            if (trendingSongList != null && trendingSongList.isNotEmpty()){
                HorizontalScrollableNewScreenSection2(
                    title = "Trending Song",
                    iconHeader = R.drawable.baseline_chevron_right_24,
                    items = trendingSongList,
                    itemWidth = 300.dp,
                    sectionHeight = 260.dp,
                    onItemClick = { musicItem ->
                        playerViewModel.resetPlaylist();
                        playerViewModel.playSong(musicItem, context)
                    }
                )
            }

            val topMusicVideos = trendingItem?.videoPlaylist?.videosToMusicItems();
            if (topMusicVideos != null && topMusicVideos.isNotEmpty()) {
                HorizontalScrollableNewScreenSection3(
                    title = "Top music videos",
                    iconHeader = R.drawable.baseline_chevron_right_24,
                    items = topMusicVideos,
                    itemWidth = 150.dp,
                    sectionHeight = 220.dp,
                    onItemClick = { musicItem ->
                        Log.d("LogNav", "TYPE = 0")
                        playerViewModel.resetPlaylist();
                        playerViewModel.playSong(musicItem, context)
                    }
                )
            }

            val newAlbumReleases = newReleases.getOrNull(1)?.contents?.mapNotNull {
                if (it != null) {
                    return@mapNotNull fromHomeContent(it, false);
                }
                return@mapNotNull null;
            };
            if (!newAlbumReleases.isNullOrEmpty()) {
                HorizontalScrollableNewScreenSection3(
                    title = "New Album Releases",
                    iconHeader = R.drawable.baseline_chevron_right_24,
                    items = newAlbumReleases,
                    itemWidth = 150.dp,
                    sectionHeight = 220.dp,
                    onItemClick = { }
                )
            }

            val newMusicVideos = newReleases.getOrNull(2)?.contents?.mapNotNull {
                if (it != null) {
                    return@mapNotNull fromHomeContent(it, false);
                }
                return@mapNotNull null;
            }
            if (!newMusicVideos.isNullOrEmpty()) {
                HorizontalScrollableNewScreenSection3(
                    title = "New music videos",
                    iconHeader = R.drawable.baseline_chevron_right_24,
                    items =  newMusicVideos,
                    itemWidth = 150.dp,
                    sectionHeight = 220.dp,
                    onItemClick = { musicItem ->
                        Log.d("LogNav", "TYPE = 0")
                        playerViewModel.resetPlaylist();
                        playerViewModel.playSong(musicItem, context)
                    }
                )
            }
//
//            HorizontalScrollableNewScreenSection2(
//                title = "Latest Songs",
//                iconHeader = R.drawable.baseline_chevron_right_24,
//                items = SongListSampleNewScreen(),
//                itemWidth = 300.dp,
//                sectionHeight = 260.dp,
//                onItemClick = { }
//            )
//
//            HorizontalScrollableNewScreenSection3(
//                title = "Everyone's Talking About",
//                iconHeader = R.drawable.baseline_chevron_right_24,
//                items = SongListSample(),
//                itemWidth = 150.dp,
//                sectionHeight = 220.dp,
//                onItemClick = { }
//            )
//
//            HorizontalScrollableNewScreenSection3(
//                title = "Daily Top 100",
//                iconHeader = R.drawable.baseline_chevron_right_24,
//                items = SongListSample(),
//                itemWidth = 150.dp,
//                sectionHeight = 220.dp,
//                onItemClick = { }
//            )
//
//            HorizontalScrollableNewScreenSection3(
//                title = "City Charts",
//                iconHeader = R.drawable.baseline_chevron_right_24,
//                items = SongListSample(),
//                itemWidth = 150.dp,
//                sectionHeight = 220.dp,
//                onItemClick = { }
//            )
        }
    }
}
