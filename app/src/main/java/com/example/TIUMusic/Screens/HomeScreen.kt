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
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.toMusicItemsList

@Composable
fun HomeScreen(
    navController: NavHostController,
    ytMusicViewModel: YtmusicViewModel,
    onTabSelected: (Int) -> Unit = {},
    onItemClick: (MusicItem) -> Unit = {},
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
                    items = toMusicItemsList(it.contents),
                    itemWidth = 200.dp,
                    sectionHeight = 280.dp,
                    onItemClick = onItemClick
                );
            }
        }
    }
}

