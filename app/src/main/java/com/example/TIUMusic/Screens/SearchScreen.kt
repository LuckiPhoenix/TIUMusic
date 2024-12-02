package com.example.TIUMusic.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.TIUMusic.SongData.MusicItem

val heightItemCategorySearch = 140.dp

@Composable
fun SearchScreen(
    navController: NavController,
    onTabSelected: (Int) -> Unit,
    onClick: (MusicItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScrollableSearchScreen (
        searchViewModel = hiltViewModel(),
        onClick = onClick,
        onTabSelected = onTabSelected
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
//            Row {
//                Box(
//                    modifier = Modifier
//                        .padding(20.dp, 70.dp, 6.dp, 0.dp)
//                        .width(0.dp)
//                        .weight(1F)
//                        .height(heightItemCategorySearch)
//                ) {
//                    AsyncImage(
//                        model = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
//                        contentDescription = "Album art for",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(180.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFF282828))
//                    )
//
//                    Text(
//                        text = "R&B".uppercase(),
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(10.dp),
//                        color = Color.White,
//                        fontSize = 16.sp,
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .padding(6.dp, 70.dp, 20.dp, 0.dp)
//                        .width(0.dp)
//                        .weight(1F)
//                        .height(heightItemCategorySearch)
//                ) {
//
//                    AsyncImage(
//                        model = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
//                        contentDescription = "Album art for",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(180.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFF282828))
//                    )
//
//                    Text(
//                        text = "Apple Music Live",
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(10.dp),
//                        color = Color.White,
//                        fontSize = 16.sp,
//                    )
//                }
//            }
//
//            Row {
//                Box(
//                    modifier = Modifier
//                        .padding(20.dp, 20.dp, 6.dp, 0.dp)
//                        .width(0.dp)
//                        .weight(1F)
//                        .height(heightItemCategorySearch)
//                ) {
//
//                    AsyncImage(
//                        model = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
//                        contentDescription = "Album art for",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(180.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFF282828))
//                    )
//
//                    Text(
//                        text = "Vietnamese Music",
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(10.dp),
//                        color = Color.White,
//                        fontSize = 16.sp,
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .padding(6.dp, 20.dp, 20.dp, 0.dp)
//                        .width(0.dp)
//                        .weight(1F)
//                        .height(heightItemCategorySearch)
//                ) {
//
//                    AsyncImage(
//                        model = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
//                        contentDescription = "Album art for",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(180.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFF282828))
//                    )
//
//                    Text(
//                        text = "K-Pop",
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(10.dp),
//                        color = Color.White,
//                        fontSize = 16.sp,
//                    )
//                }
//            }
//
//            Row {
//                Box(
//                    modifier = Modifier
//                        .padding(20.dp, 20.dp, 6.dp, 0.dp)
//                        .width(0.dp)
//                        .weight(1F)
//                        .height(heightItemCategorySearch)
//                ) {
//
//                    AsyncImage(
//                        model = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
//                        contentDescription = "Album art for",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(180.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFF282828))
//                    )
//
//                    Text(
//                        text = "Pop",
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(10.dp),
//                        color = Color.White,
//                        fontSize = 16.sp,
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .padding(6.dp, 20.dp, 20.dp, 0.dp)
//                        .width(0.dp)
//                        .weight(1F)
//                        .height(heightItemCategorySearch)
//                ) {
//
//                    AsyncImage(
//                        model = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
//                        contentDescription = "Album art for",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(180.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFF282828))
//                    )
//
//                    Text(
//                        text = "Charts",
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(10.dp),
//                        color = Color.White,
//                        fontSize = 16.sp,
//                    )
//                }
//            }
//
//            Row {
//                Box(
//                    modifier = Modifier
//                        .padding(20.dp, 20.dp, 6.dp, 0.dp)
//                        .width(0.dp)
//                        .weight(1F)
//                        .height(heightItemCategorySearch)
//                ) {
//
//                    AsyncImage(
//                        model = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
//                        contentDescription = "Album art for",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(180.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFF282828))
//                    )
//
//                    Text(
//                        text = "Hits",
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(10.dp),
//                        color = Color.White,
//                        fontSize = 16.sp,
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .padding(6.dp, 20.dp, 20.dp, 0.dp)
//                        .width(0.dp)
//                        .weight(1F)
//                        .height(heightItemCategorySearch)
//                ) {
//
//                    AsyncImage(
//                        model = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
//                        contentDescription = "Album art for",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(180.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                            .background(Color(0xFF282828))
//                    )
//
//                    Text(
//                        text = "Spatial Audio",
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .padding(10.dp),
//                        color = Color.White,
//                        fontSize = 16.sp,
//                    )
//                }
//            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    val navController = rememberNavController()
    SearchScreen(navController = navController, {}, onClick = {})
}