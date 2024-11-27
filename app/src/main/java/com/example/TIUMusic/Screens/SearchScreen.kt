package com.example.TIUMusic.Screens

import android.util.Log
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.Query
import coil.compose.AsyncImage
import com.example.TIUMusic.Libs.YoutubeLib.Ytmusic
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Libs.YoutubeLib.models.YouTubeClient
import com.example.TIUMusic.Login.MyTextField
import com.example.TIUMusic.ui.theme.BackgroundColor

val heightItemCategorySearch = 140.dp

@Composable
fun SearchScreen(
    title: String = "Search",
    selectedTab: Int = 3,
    onTabSelected: (Int) -> Unit = {},
    viewModel: YtmusicViewModel = hiltViewModel(),
    navController: NavController
) {
    //DEMO search
    val search = viewModel.performSearch("Shape of you")
    val isLoading by viewModel.loading.collectAsState()

    // Loading Indicator
    if (isLoading) {
        CircularProgressIndicator()
    }

    // Hiển thị kết quả tìm kiếm

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
    var query: String = "Shape of you"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content area
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .background(BackgroundColor)
                    .padding(top = expandedHeight)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        textStyle = TextStyle(color = Color.LightGray, fontSize = 16.sp),
                        cursorBrush = SolidColor(Color.LightGray),
                        singleLine = true,
                        modifier = Modifier
//                        .weight(1f)
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .background(Color.Transparent)
                    )
                }
                Button(
                    onClick = {
                        viewModel.performSearch("Shap")
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Search")
                }
            }

            // Top app bar
            AnimatedTopAppBar(
                title = title,
                alpha = alpha,
                translationX = translationX,
                titleSize = titleSize.sp,
                height = height
            )


            // Bottom navigation
            CustomBottomNavigation(
                selectedTab = selectedTab,
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
    val navController = rememberNavController()
    SearchScreen(navController = navController)
}