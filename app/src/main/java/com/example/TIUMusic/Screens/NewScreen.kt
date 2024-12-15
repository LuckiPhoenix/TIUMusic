package com.example.TIUMusic.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.MusicDB.MusicViewModel
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.MusicItemType
import com.example.TIUMusic.SongData.NewReleaseCard
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.SongData.fromHomeContent
import com.example.TIUMusic.SongData.musicItemSplitToRow
import kotlin.math.min


@Composable
fun NewScreen(
    navController: NavController,
    onTabSelected: (Int) -> Unit,
    onItemClick: (MusicItem) -> Unit,
    musicViewModel: MusicViewModel = hiltViewModel(),
    ytmusicViewModel: YtmusicViewModel,
    playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current;
    var newReleaseItems by remember { mutableStateOf(listOf<NewReleaseCard>()) }
    val newSongReleases by musicViewModel.getNewSongReleases(21, context).collectAsState(listOf());
    val newAlbumReleases by musicViewModel.getNewAlbumsReleases(5, context).collectAsState(listOf());
//    val trendingItem by ytmusicViewModel.chart.collectAsState();
//    val newReleases by ytmusicViewModel.newReleases.collectAsState();

    LaunchedEffect(newSongReleases, newAlbumReleases) {
        val items = mutableListOf<NewReleaseCard>();
        if (newAlbumReleases.isNotEmpty()) {
            items.addAll(
                newAlbumReleases
                    .subList(0, min(newAlbumReleases.size, 2))
                    .map { NewReleaseCard(type = "Album", it) }
            )
        }
        if (newSongReleases.isNotEmpty()) {
            items.addAll(
                newSongReleases
                    .subList(0, min(newSongReleases.size, 3))
                    .map { NewReleaseCard(type = "Song", it) }
            )
        }

        newReleaseItems = items;
    }

    ScrollableScreen(
        title = "New",
        selectedTab = 1,
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            if (newSongReleases.isNotEmpty()){
                HorizontalScrollableNewScreenSection(
                    items = newReleaseItems,
                    itemWidth = 300.dp,
                    sectionHeight = 300.dp,
                    onItemClick = { musicItem ->
                        onItemClick(musicItem)
                    }
                )
            }

            if (newSongReleases.isNotEmpty()){
                HorizontalScrollableNewScreenSection2(
                    title = "New Song Releases",
                    iconHeader = R.drawable.baseline_chevron_right_24,
                    items = musicItemSplitToRow(newSongReleases, 3),
                    itemWidth = 300.dp,
                    sectionHeight = 260.dp,
                    onItemClick = { musicItem ->
                        onItemClick(musicItem);
                    }
                )
            }

            if (newAlbumReleases.isNotEmpty()) {
                HorizontalScrollableNewScreenSection3(
                    title = "New Album Releases",
                    iconHeader = R.drawable.baseline_chevron_right_24,
                    items = newAlbumReleases,
                    itemWidth = 150.dp,
                    sectionHeight = 220.dp,
                    onItemClick = { it ->
                        onItemClick(it);
                    }
                )
            }

        }
    }
}
