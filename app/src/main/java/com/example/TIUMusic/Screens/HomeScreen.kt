package com.example.TIUMusic.Screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.getBasedOnRecent
import com.example.TIUMusic.SongData.getPlaylists
import com.example.TIUMusic.SongData.getRecentItems
import com.example.TIUMusic.SongData.getTopPicks

@Composable
fun HomeScreen(
    navController: NavHostController,
    onTabSelected: (Int) -> Unit = {},
    onPlaylistClick: (MusicItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    ScrollableScreen(
        title = "Home",
        selectedTab = 0,
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            HorizontalScrollableSection(
                title = "Top picks for you",
                items = getTopPicks(),
                itemWidth = 200.dp,
                sectionHeight = 280.dp,
                onItemClick = onPlaylistClick
            )

            HorizontalScrollableSection(
                title = "Recent",
                items = getRecentItems(),
                onItemClick = onPlaylistClick
            )

            HorizontalScrollableSection(
                title = "Based on recent activity",
                items = getBasedOnRecent(),
                onItemClick = onPlaylistClick
            )

            HorizontalScrollableSection(
                title = "Made for you",
                items = getPlaylists(),
                onItemClick = onPlaylistClick
            )
        }
    }
}