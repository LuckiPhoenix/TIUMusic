package com.example.TIUMusic.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.getTopPicks

@Composable
fun NewScreen(navController: NavController, onTabSelected: (Int) -> Unit, onPlaylistClick: (MusicItem) -> Unit) {

    ScrollableScreen(
        title = "New",
        selectedTab = 1,
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            HorizontalScrollableNewScreenSection(
                items = getTopPicks(),
                itemWidth = 300.dp,
                sectionHeight = 300.dp,
                onItemClick = { }
            )

            HorizontalScrollableNewScreenSection2(
                title = "Trending Song",
                iconHeader = R.drawable.baseline_chevron_right_24,
                items = SongListSampleNewScreen(),
                itemWidth = 300.dp,
                sectionHeight = 260.dp,
                onItemClick = { }
            )

            HorizontalScrollableNewScreenSection3(
                title = "New Releases",
                iconHeader = R.drawable.baseline_chevron_right_24,
                items = SongListSample(),
                itemWidth = 150.dp,
                sectionHeight = 220.dp,
                onItemClick = { }
            )

            HorizontalScrollableNewScreenSection3(
                title = "Viet music",
                iconHeader = R.drawable.baseline_chevron_right_24,
                items = SongListSample(),
                itemWidth = 150.dp,
                sectionHeight = 220.dp,
                onItemClick = { }
            )

            HorizontalScrollableNewScreenSection4(
                title = "Updated Playlist",
                iconHeader = R.drawable.baseline_chevron_right_24,
                items = SongListSampleNewScreenType4(),
                itemWidth = 150.dp,
                sectionHeight = 440.dp,
                onItemClick = { }
            )

            HorizontalScrollableNewScreenSection3(
                title = "Match Your Mood",
                iconHeader = R.drawable.baseline_chevron_right_24,
                items = SongListSample(),
                itemWidth = 150.dp,
                sectionHeight = 220.dp,
                onItemClick = { }
            )

            HorizontalScrollableNewScreenSection2(
                title = "Latest Songs",
                iconHeader = R.drawable.baseline_chevron_right_24,
                items = SongListSampleNewScreen(),
                itemWidth = 300.dp,
                sectionHeight = 260.dp,
                onItemClick = { }
            )

            HorizontalScrollableNewScreenSection3(
                title = "Everyone's Talking About",
                iconHeader = R.drawable.baseline_chevron_right_24,
                items = SongListSample(),
                itemWidth = 150.dp,
                sectionHeight = 220.dp,
                onItemClick = { }
            )

            HorizontalScrollableNewScreenSection3(
                title = "Daily Top 100",
                iconHeader = R.drawable.baseline_chevron_right_24,
                items = SongListSample(),
                itemWidth = 150.dp,
                sectionHeight = 220.dp,
                onItemClick = { }
            )

            HorizontalScrollableNewScreenSection3(
                title = "City Charts",
                iconHeader = R.drawable.baseline_chevron_right_24,
                items = SongListSample(),
                itemWidth = 150.dp,
                sectionHeight = 220.dp,
                onItemClick = { }
            )
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
