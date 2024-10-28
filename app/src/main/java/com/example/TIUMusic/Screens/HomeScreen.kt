package com.example.TIUMusic.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.PrimaryColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 64.sp) },
                modifier = Modifier.padding(vertical = 16.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        },
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(BackgroundColor)
        ) {

            SectionTitle("Top picks for you")
            LazyHorizontalGrid(
                rows = GridCells.Fixed(1),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(300.dp)
            ) {
                items(getTopPicks()) { item ->
                    AlbumCard(
                        item = item,
                        modifier = Modifier.width(240.dp),
                        imageSize = 240.dp
                    )
                }
            }


            // Recent Section
            SectionTitle("Recent")
            LazyHorizontalGrid(
                rows = GridCells.Fixed(1),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(220.dp)
            ) {
                items(getRecentItems()) { item ->
                    AlbumCard(item)
                }
            }

            // Based on Recent Songs Section
            SectionTitle("Based on recent songs")
            LazyHorizontalGrid(
                rows = GridCells.Fixed(1),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(220.dp)
            ) {
                items(getBasedOnRecent()) { item ->
                    AlbumCard(
                        item = item,
                        modifier = Modifier.width(160.dp),
                        imageSize = 160.dp
                    )
                }
            }


            // Playlists Section
            SectionTitle("Playlists for You")
            LazyHorizontalGrid(
                rows = GridCells.Fixed(1),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(220.dp)
            ) {
                items(getPlaylists()) { item ->
                    AlbumCard(item)
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomNavColor = Color(0xAA1C1C1E)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(bottomNavColor),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(
            icon = Icons.Default.Home,
            text = "Home",
            selected = selectedTab == 0,
            onSelect = { onTabSelected(0) }
        )
        NavItem(
            icon = Icons.Default.Add,
            text = "New",
            selected = selectedTab == 1,
            onSelect = { onTabSelected(1) }
        )
        NavItem(
            icon = Icons.Default.Menu,
            text = "Library",
            selected = selectedTab == 2,
            onSelect = { onTabSelected(2) }
        )
        NavItem(
            icon = Icons.Default.Search,
            text = "Search",
            selected = selectedTab == 3,
            onSelect = { onTabSelected(3) }
        )
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val SelectedColor = PrimaryColor
    val UnselectedColor = Color.LightGray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onSelect() }
            .padding(horizontal = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(36.dp),
            tint = if (selected) SelectedColor else UnselectedColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (selected) SelectedColor else UnselectedColor
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun AlbumCard(
    item: MusicItem,
    modifier: Modifier = Modifier,
    imageSize: Dp = 160.dp
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF282828))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Text(
            text = item.artist,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}


/*
* Placeholder data classes for MusicItem, will replace soon
* Do not use the following code in production
* */
data class MusicItem(
    val title: String,
    val artist: String,
)

// Sample data functions
private fun getTopPicks() = List(6) {
    MusicItem("Top Album ${it + 1}", "Artist ${it + 1}")
}

private fun getRecentItems() = List(6) {
    MusicItem("Recent Album ${it + 1}", "Artist ${it + 1}")
}

private fun getBasedOnRecent() = List(6) {
    MusicItem("Recommended ${it + 1}", "Similar Artist ${it + 1}")
}

private fun getPlaylists() = List(6) {
    MusicItem("Playlist ${it + 1}", "Various Artists")
}


@Preview
@Composable
fun MusicScreenPreview() {
    HomeScreen()
}