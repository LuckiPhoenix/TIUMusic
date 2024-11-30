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
import androidx.navigation.NavController
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.NewReleaseCard
import kotlinx.coroutines.launch


@Composable
fun NewScreen(
    navController: NavController,
    onTabSelected: (Int) -> Unit,
    onPlaylistClick: (MusicItem) -> Unit,
    ytmusicViewModel: YtmusicViewModel
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
                                MusicItem(
                                    id = it.videoId ?: "",
                                    title = it.title,
                                    artist = it.artists?.firstOrNull()?.name ?: "",
                                    imageUrl = it.thumbnails.lastOrNull()?.url ?: ""
                                )
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
                    onItemClick = { }
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
                    onItemClick = { }
                )
            }

            val newAlbumReleases = newReleases.getOrNull(1)?.contents?.mapNotNull {
                MusicItem(
                    id = it?.videoId ?: "",
                    title = it?.title ?: "",
                    artist = it?.artists?.firstOrNull()?.name ?: "",
                    imageUrl = it?.thumbnails?.lastOrNull()?.url ?: "",
                )
            };
            if (newAlbumReleases != null) {
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
                MusicItem(
                    id = it?.videoId ?: "",
                    title = it?.title ?: "",
                    artist = it?.artists?.firstOrNull()?.name ?: "",
                    imageUrl = it?.thumbnails?.lastOrNull()?.url ?: "",
                )
            }
            if (newMusicVideos != null && newMusicVideos.isNotEmpty()) {
                HorizontalScrollableNewScreenSection3(
                    title = "New music videos",
                    iconHeader = R.drawable.baseline_chevron_right_24,
                    items =  newMusicVideos,
                    itemWidth = 150.dp,
                    sectionHeight = 220.dp,
                    onItemClick = { }
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

fun SongListSampleNewScreen(): List<List<MusicItem>> {
    return listOf(
        listOf(
            MusicItem(
                "01",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "02",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "03",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "04",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
        ),
        listOf(
            MusicItem(
                "05",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "06",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "07",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "08",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
        ),
        listOf(
            MusicItem(
                "09",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "10",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "11",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "12",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
        ),
        listOf(
            MusicItem(
                "13",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "14",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "15",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            )
        )
    )
}

fun SongListSampleNewScreenType4(): List<List<MusicItem>> {
    return listOf(
        listOf(
            MusicItem(
                "01",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "02",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
        ),
        listOf(
            MusicItem(
                "03",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "04",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
        ),
        listOf(
            MusicItem(
                "05",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "06",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
        ),
        listOf(
            MusicItem(
                "07",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
            MusicItem(
                "08",
                "I Miss You",
                "Adele",
                "https://i1.sndcdn.com/artworks-v08j7vI5enr5-0-t500x500.png"
            ),
        )
    )
}
