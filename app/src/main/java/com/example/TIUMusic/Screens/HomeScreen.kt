package com.example.TIUMusic.Screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeContent
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeItem
import com.example.TIUMusic.MusicDB.MusicViewModel
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.toMusicItemsList
import com.example.TIUMusic.Utils.nameToRID

data class HomeTest(
    val title : String,
    val contents : List<MusicItem>,
)

@Composable
fun HomeScreen(
    navController: NavHostController,
    ytMusicViewModel: YtmusicViewModel,
    musicViewModel : MusicViewModel = MusicViewModel(LocalContext.current),
    onTabSelected: (Int) -> Unit = {},
    onItemClick: (MusicItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current;
    val homeItems by remember {
        mutableStateOf(
            listOf(
                HomeTest(
                    contents =
                        musicViewModel.getAlbums(context).map {
                            MusicItem(
                                title = it.title,
                                artist = it.artist,
                                videoId = "",
                                type = 1,
                                imageUrl = it.imageUri,
                                imageRId = nameToRID(it.imageUri, "raw", context),
                                browseId = it.id.toString(),
                            )
                        }
                    ,
                    title = "Hello"
                )
            )
        )
    }

    ScrollableScreen(
        title = "Home",
        selectedTab = 0,
        itemCount = homeItems.size,
        fetchContinuation = {
        },
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            homeItems.forEach {
                HorizontalScrollableSection(
                    title = it.title,
                    items = it.contents,
                    itemWidth = 200.dp,
                    sectionHeight = 280.dp,
                    onItemClick = onItemClick
                );
            }
        }
    }
}

