package com.example.TIUMusic.Screens

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.TIUMusic.R
import com.example.TIUMusic.ui.theme.BackgroundColor



@Composable
fun SearchScreen(
    onTabSelected: (Int) -> Unit = {},
    navController: NavController
) {
    val scrollState = rememberScrollState()
    val windowSize = rememberWindowSize()

    // Transition variables
    var isScrolled by remember { mutableStateOf(false) }
    val transitionState = updateTransition(targetState = isScrolled, label = "AppBarTransition")

    // Calculate dynamic values
    val expandedHeight = Dimensions.topBarExpandedHeight()
    val collapsedHeight = Dimensions.topBarCollapsedHeight()
    val expandedTitleSize = Dimensions.expandedTitleSize()
    val collapsedTitleSize = Dimensions.collapsedTitleSize()
    val bottomNavHeight = 56.dp // Define bottom nav height

    // Animation values
    val alpha by transitionState.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "Alpha"
    ) { state -> if (state) 0.9f else 1f }

    val translationX by transitionState.animateDp(
        transitionSpec = { tween(durationMillis = 500) },
        label = "TranslationX"
    ) { state ->
        if (state) {
            when (windowSize) {
                WindowSize.COMPACT -> (LocalConfiguration.current.screenWidthDp.dp / 2) - 52.dp
                WindowSize.MEDIUM -> (LocalConfiguration.current.screenWidthDp.dp / 2) - 48.dp
            }
        } else 0.dp
    }

    val titleSize by transitionState.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "TextSize"
    ) { state ->
        if (state) collapsedTitleSize.value else expandedTitleSize.value
    }

    val height by transitionState.animateDp(
        transitionSpec = { tween(durationMillis = 300) },
        label = "height"
    ) { state -> if (state) collapsedHeight else expandedHeight }

    LaunchedEffect(scrollState.value) {
        isScrolled = scrollState.value > expandedHeight.value
    }
    var query by remember { mutableStateOf("Search here") }
    var active by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content area
            Column(
                modifier = Modifier
                    .background(BackgroundColor)
                    .verticalScroll(scrollState)
                    .padding(top = expandedHeight)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                            .background(Color.DarkGray),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            textStyle = TextStyle(
                                color = if (active) Color.LightGray else Color.Gray,
                                fontSize = 16.sp
                            ),
                            cursorBrush = SolidColor(Color.LightGray),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 12.dp)
                                        .background(Color.Transparent)
                                ) {
                                    if (query == "Search here" && !active) {
                                        //nothing
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    active = focusState.isFocused
                                    if (focusState.isFocused && query == "Search here") {
                                        query = ""
                                    }
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row() {
                        CategoryCard("RnB",{}, modifier = Modifier.weight(1f))
                        CategoryCard("Live",{}, modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        CategoryCard("RnB",{}, modifier = Modifier.weight(1f))
                        CategoryCard("Live",{}, modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        CategoryCard("RnB",{}, modifier = Modifier.weight(1f))
                        CategoryCard("Live",{}, modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        CategoryCard("RnB",{}, modifier = Modifier.weight(1f))
                        CategoryCard("Live",{}, modifier = Modifier.weight(1f))
                    }
                }
            }
                // Top app bar
                AnimatedTopAppBar(
                    title = "Search",
                    alpha = alpha,
                    translationX = translationX,
                    titleSize = titleSize.sp,
                    height = height
                )

            // Bottom navigation
            CustomBottomNavigation(
                selectedTab = 3,
                onTabSelected = onTabSelected,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .padding(8.dp)
            .height(140.dp)
            .clickable { onClick() }
    ) {

        AsyncImage(
            model = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
            contentDescription = "Album art",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF282828))
        )

        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp),
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    val navController = rememberNavController()
    SearchScreen(
        onTabSelected = {},
        navController
    )
}