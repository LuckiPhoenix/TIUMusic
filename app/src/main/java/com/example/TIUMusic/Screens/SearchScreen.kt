package com.example.TIUMusic.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.TIUMusic.Libs.YoutubeLib.YouTube
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Libs.YoutubeLib.getYoutubeSmallThumbnail
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.toMusicItemsList

val heightItemCategorySearch = 140.dp

@Composable
fun SearchScreen(
    navController: NavController,
    onTabSelected: (Int) -> Unit,
    onClick: (MusicItem) -> Unit,
    modifier: Modifier = Modifier,
    searchViewModel: YtmusicViewModel
) {
    val searchResults by searchViewModel.searchResults.collectAsState()
    LaunchedEffect(Unit) {
        searchViewModel.fetchMoodAndGenres()
    }

    ScrollableSearchScreen (
        searchViewModel = searchViewModel,
        onClick = onClick,
        onTabSelected = onTabSelected
    ) {paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.height(20.dp))

            if(searchResults.isEmpty()){
                //SuggestScreen(searchViewModel)
                Spacer(modifier = Modifier.height(40.dp))
                MoodScreen(searchViewModel)
            }
            else{
                Spacer(modifier = Modifier.height(75.dp))
                searchResults.forEach {
                    Column(modifier = Modifier.clickable(
                        onClick = { onClick(it) }
                    )) {
                        Row(modifier = Modifier.padding(all = 10.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically) {
                            var thumbnailURL = it.imageUrl;
                            if (it.videoId != "")
                                thumbnailURL = getYoutubeSmallThumbnail(it.videoId);
                            AsyncImage(
                                model = thumbnailURL,
                                contentDescription = "Album art for",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF282828))
                            )

                            Column(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .width(0.dp)
                                    .weight(1F)
                            ) {
                                var type = ""
                                when (it.type){
                                    0 -> type += "Song • "
                                    1 -> type += "Playlist • "
                                    2 -> type += "Album • "
                                    3 -> type += "Artist • "
                                }
                                Text(
                                    text = it.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(all = 4.dp)
                                )
                                Text(
                                    text = type + it.artist,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }

                            Icon(
                                modifier = Modifier.padding(all = 10.dp),
                                painter = painterResource(R.drawable.baseline_more_vert_24),
                                contentDescription = "Suggestion Icon",
                                tint = Color.White
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 4.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.Gray)
                        ) {}
                    }
                }
            }
        }
    }
}
@Composable
fun SuggestScreen(
    viewModel: YtmusicViewModel
){
    Row {
        Box(
            modifier = Modifier
                .padding(20.dp, WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 30.dp, 6.dp, 0.dp)
                .width(0.dp)
                .weight(1F)
                .height(heightItemCategorySearch)
        ) {

            AsyncImage(
                model = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/cb/bb/c7/cbbbc705-8632-8b71-5fbd-a913dc495c5a/198846825281.jpg/316x316bb.webp",
                contentDescription = "Album art for",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )

            Text(
                text = "R&B".uppercase(),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
            )
        }
        Box(
            modifier = Modifier
                .padding(6.dp, WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 30.dp, 20.dp, 0.dp)
                .width(0.dp)
                .weight(1F)
                .height(heightItemCategorySearch)
        ) {

            AsyncImage(
                model = "https://www.linearity.io/blog/content/images/2023/11/best-album-covers-cover.png",
                contentDescription = "Album art for",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )

            Text(
                text = "Live",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
            )
        }
    }

    Row {
        Box(
            modifier = Modifier
                .padding(20.dp, 20.dp, 6.dp, 0.dp)
                .width(0.dp)
                .weight(1F)
                .height(heightItemCategorySearch)
        ) {

            AsyncImage(
                model = "https://yt3.googleusercontent.com/oN0p3-PD3HUzn2KbMm4fVhvRrKtJhodGlwocI184BBSpybcQIphSeh3Z0i7WBgTq7e12yKxb=s900-c-k-c0x00ffffff-no-rj",
                contentDescription = "Album art for",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )

            Text(
                text = "Vietnamese Music",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
            )
        }
        Box(
            modifier = Modifier
                .padding(6.dp, 20.dp, 20.dp, 0.dp)
                .width(0.dp)
                .weight(1F)
                .height(heightItemCategorySearch)
        ) {

            AsyncImage(
                model = "https://preview.redd.it/241018-ros%C3%A9-bruno-mars-apt-for-apple-music-v0-1fnb0oja1gvd1.jpeg?width=640&crop=smart&auto=webp&s=2d2ba2f63eb4c3b1f551762b7f4f14b68bfbfc2d",
                contentDescription = "Album art for",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )

            Text(
                text = "K-Pop",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
            )
        }
    }

    Row {
        Box(
            modifier = Modifier
                .padding(20.dp, 20.dp, 6.dp, 0.dp)
                .width(0.dp)
                .weight(1F)
                .height(heightItemCategorySearch)
        ) {

            AsyncImage(
                model = "https://danviet.mediacdn.vn/296231569849192448/2024/2/29/swifttaylorap102122-1709208054815543939904.jpg",
                contentDescription = "Album art for",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )

            Text(
                text = "Pop",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
            )
        }
        Box(
            modifier = Modifier
                .padding(6.dp, 20.dp, 20.dp, 0.dp)
                .width(0.dp)
                .weight(1F)
                .height(heightItemCategorySearch)
        ) {

            AsyncImage(
                model = "https://upload.wikimedia.org/wikipedia/vi/3/3d/When_We_All_Fall_Asleep%2C_Where_Do_We_Go%3F.jpeg",
                contentDescription = "Album art for",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )

            Text(
                text = "Alternative",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
            )
        }
    }

    Row {
        Box(
            modifier = Modifier
                .padding(20.dp, 20.dp, 6.dp, 0.dp)
                .width(0.dp)
                .weight(1F)
                .padding(bottom = 80.dp)
                .height(heightItemCategorySearch)
        ) {

            AsyncImage(
                model = "https://upload.wikimedia.org/wikipedia/vi/2/2f/AbbeyRoadcover.jpg",
                contentDescription = "Album art for",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )

            Text(
                text = "Rock",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
            )
        }
        Box(
            modifier = Modifier
                .padding(6.dp, 20.dp, 20.dp, 0.dp)
                .width(0.dp)
                .weight(1F)
                .height(heightItemCategorySearch)
        ) {

            AsyncImage(
                model = "https://yt3.googleusercontent.com/-1Bt8eufq601KzmA0Rk1yrIzz372UDDeBkIpWytlztkcAyh1w6g2bNfXVnR6hadnezvUCq0XICA=s900-c-k-c0x00ffffff-no-rj",
                contentDescription = "Album art for",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )

            Text(
                text = "Hip hop/Rap",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                color = Color.White,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun MoodScreen(
    viewModel: YtmusicViewModel
) {
    val dataMood by viewModel.moodList.collectAsState()

    Column(modifier = Modifier.fillMaxSize()){
        dataMood.forEach { data ->
            Text(
                text = data.first,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp),
            )
            data.second.chunked(2).forEach{chunk ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    chunk.forEach{
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF292929))
                                .clickable {  }
                        ) {
                            Box(
                                modifier = Modifier.align(Alignment.CenterStart)
                                    .fillMaxHeight()
                                    .width(8.dp)
                                    .background(Color(it.color))
                            )
                            Text(
                                text = it.title,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(8.dp)
                                    .padding(start = 8.dp)
                                    .align(Alignment.CenterStart),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MoodPlaylistScreen(
    navController: NavHostController,
    ytMusicViewModel: YtmusicViewModel,
    onTabSelected: (Int) -> Unit = {},
    onItemClick: (MusicItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    ScrollableScreen(
        title = "Home",
        selectedTab = 0,
        itemCount = homeItems.size,
        fetchContinuation = {
            ytMusicViewModel.getHomeContinuation(context);
        },
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .height(600.dp), // Adjust height as needed
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(userPlaylists) { item ->
                    AlbumCard(
                        item = item,
                        modifier = Modifier,
                        imageSize = 180.dp,
                        onClick = {
                            onItemClick(item)
                        }
                    )
                }
                item {Spacer(modifier = Modifier.height(88.dp))}
            }
            AnimatedTopAppBar(
                title = ,
                alpha = alpha,
                translationX = translationX,
                titleSize = titleSize.sp,
                height = height
            )


            // Bottom navigation
            CustomBottomNavigation(
                selectedTab = 2,
                onTabSelected = onTabSelected,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SuggestScreen(viewModel = hiltViewModel())
}