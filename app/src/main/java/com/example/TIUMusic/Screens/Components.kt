package com.example.TIUMusic.Screens


import android.media.AudioManager
import androidx.annotation.DrawableRes
import androidx.compose.animation.Animatable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.TIUMusic.Libs.MediaPlayer.AudioPlayerView
import com.example.TIUMusic.Libs.MediaPlayer.MediaViewModel
import com.example.TIUMusic.Libs.Visualizer.VisualizerViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeView
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Libs.YoutubeLib.getYoutubeSmallThumbnail
import com.example.TIUMusic.MainActivity
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.NewReleaseCard
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.ui.theme.ArtistNameColor
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.PrimaryColor
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/*
các components reusable phải được declare ở đây
 */

/*Canvas cho các screens, top bar đã được animate
  bottom bar thì chỗ selectedTab cho 0123 để highlight màu tab nào
  công thức là:
  ScrollableScreen(
        title = "Gì đó",
        selectedTab = 0 1 2 3 gì đó,
        onTabSelected = onTabSelected
    ) { paddingValues ->
        bắt đầu declare UI chỗ này
        phải có padding là paddingValues cho ko lỗi
    }
 */
@Composable
fun ScrollableScreen(
    title: String,
    selectedTab: Int = 0,
    itemCount : Int = 0,
    onTabSelected: (Int) -> Unit = {},
    fetchContinuation : () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollState = rememberScrollState()
    val windowSize = rememberWindowSize()
    val fetchBufferItem = 1;
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

    var reachedBottom = false;
    if (itemCount != 0 && scrollState.maxValue != 0) {
        val sizePerItem = scrollState.maxValue / itemCount
        reachedBottom = scrollState.value / sizePerItem >= itemCount - fetchBufferItem;
    }

    println("${reachedBottom}  ${scrollState.value} max: ${scrollState.maxValue}")

    LaunchedEffect(scrollState.value) {
        isScrolled = scrollState.value > expandedHeight.value
        if (reachedBottom)
            fetchContinuation();
    }

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
                content(
                    PaddingValues(
                        bottom = bottomNavHeight + 80.dp, // Add extra padding for NowPlayingSheet
                    )
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableSearchScreen(
    searchViewModel: YtmusicViewModel,
    onClick: (MusicItem) -> Unit,
    onTabSelected: (Int) -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
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
                content(
                    PaddingValues(
                        bottom = bottomNavHeight + 80.dp, // Add extra padding for NowPlayingSheet
                    )
                )
            }

            var text by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }

            val searchResults by searchViewModel.searchResults.collectAsState()
            val searchSuggests by searchViewModel.searchSuggests.collectAsState()
            val isLoading by searchViewModel.loading.collectAsState()

            val searchSuggestions = listOf("Top results", "Songs", "Albums", "Playlists", "Artists" )
            var selectedSearchSuggestion = searchViewModel.searchFiler.collectAsState()

            Box(
                modifier = Modifier
                    .background(BackgroundColor)
            ) {
                // Top app bar
                AnimatedTopAppBar(
                    title = "Search",
                    alpha = alpha,
                    translationX = translationX,
                    titleSize = titleSize.sp,
                    height = height
                )
                Column(
                    modifier = Modifier
                        .padding(top = if(!active) height - 60.dp else 0.dp, bottom = 10.dp)
                ) {
                    val containerColor = remember { Animatable(Color.White) }
                    LaunchedEffect(active) {
                        // Thực hiện animation khi trạng thái active thay đổi
                        containerColor.animateTo(
                            targetValue = if (active) BackgroundColor else Color.White,
                            animationSpec = tween(durationMillis = 300)
                        )
                    }
                    SearchBar(
                        query = text,
                        onQueryChange = {
                            text = it
                            searchViewModel.suggestSearch(it) // Gửi truy vấn tìm kiếm
                            searchViewModel.performSearch(it)
                        },
                        onSearch = {
                            active = false
                            searchViewModel.performSearch(text)
                        },
                        active = active,
                        onActiveChange = {
                            searchViewModel.updateSearchFilter("Top results")
                            active = it
                        },
                        placeholder = {
                            Text(
                                text = "Artists, Songs, Lyrics, and More",
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                modifier = Modifier.clickable {
                                    active = false
                                    searchViewModel.performSearch(text)
                                }
                            )
                        },
                        trailingIcon = {
                            if(text != ""){
                                IconButton(onClick = {
                                    text = ""
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Icon"
                                    )
                                }
                            }
                        },
                        colors = SearchBarDefaults.colors(
                            containerColor = containerColor.value,
                            dividerColor = Color.LightGray,
                            inputFieldColors = SearchBarDefaults.inputFieldColors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.Black,

                                focusedLeadingIconColor = Color.White,
                                unfocusedLeadingIconColor = Color.Black,

                                focusedTrailingIconColor = Color.White,
                                unfocusedTrailingIconColor = Color.Black,

                                focusedPlaceholderColor = Color.LightGray,
                                unfocusedPlaceholderColor = Color.DarkGray,
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingValues(top = 15.dp, bottom = 7.dp, start = 16.dp, end = 16.dp)),
                        shape = RoundedCornerShape(8.dp)
                    ) { searchSuggests.forEach{
                            Row(
                                modifier = Modifier
                                    .padding(all = 10.dp)
                                    .clickable(onClick = {
                                        text = it
                                        active = false
                                        searchViewModel.performSearch(it)
                                    }),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon",
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(20.dp),
                                    tint = Color.Gray
                                )
                                Text(
                                    text = it,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(all = 4.dp),
                                    fontSize = 16.sp
                                )
                            }
                        }
                        searchResults.forEach {
                            if(it.type == 0){
                                Row(modifier = Modifier
                                    .padding(all = 10.dp)
                                    .clickable(onClick = {
                                        onClick(it)
                                        active = false
                                    }),
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
                                        }
                                        it.title.let { it1 ->
                                            Text(
                                                text = it1,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(all = 4.dp)
                                            )
                                        }
                                        it.artist.let { it1 ->
                                            Text(
                                                text = type + it1,
                                                fontSize = 14.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(searchResults.isNotEmpty()){
                        LazyHorizontalGrid(
                            rows = GridCells.Fixed(1),
                            contentPadding = PaddingValues(horizontal = Dimensions.contentPadding()),
                            horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing()),
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .height(30.dp)
                        ) {
                            items(searchSuggestions) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchViewModel.updateSearchFilter(item)
                                            searchViewModel.performSearch(text)
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardColors(
                                        Color.Gray.copy(alpha = if (selectedSearchSuggestion.value == item) 0.8F else 0.2F),
                                        Color.White.copy(alpha = 0.8F),
                                        Color.Gray,
                                        Color.Black
                                    )
                                ) {
                                    Text(
                                        text = item,
                                        modifier = Modifier
                                            .height(30.dp)
                                            .padding(horizontal = 20.dp)
                                            .wrapContentHeight(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

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
fun AnimatedTopAppBar(
    title: String,
    alpha: Float,
    translationX: Dp,
    titleSize: TextUnit,
    height: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(BackgroundColor.copy(alpha = alpha))
            .offset(x = translationX)
            .padding(
                start = 32.dp,
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding()
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            style = TextStyle(
                color = Color.White,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun CustomBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomNavColor = Color(0xF01C1C1E)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(bottomNavColor)
            .padding(WindowInsets.navigationBars.asPaddingValues()),
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
            icon = Icons.Default.Star,
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
    val selectedColor = PrimaryColor
    val unselectedColor = Color.LightGray

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
            modifier = Modifier.size(24.dp),
            tint = if (selected) selectedColor else unselectedColor
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (selected) selectedColor else unselectedColor
        )
    }
}

/*
cái này là danh sách nhạc hoặc playlist
hiện tại item đang dùng tạm để show, tương lai sẽ thay đổi
 */

@Composable
fun HorizontalScrollableSection(
    title: String,
    items: List<MusicItem>,
    itemWidth: Dp? = null,
    sectionHeight: Dp? = null,
    onItemClick: (MusicItem) -> Unit = {}  // Add click handler
) {
    val windowSize = rememberWindowSize()

    val calculatedItemWidth = itemWidth ?: when (windowSize) {
        WindowSize.COMPACT -> 160.dp
        WindowSize.MEDIUM -> 180.dp
    }
    val calculatedSectionHeight = sectionHeight ?: (calculatedItemWidth + 80.dp)

    Column {
        SectionTitle(title)
        // Define the state first
        val state = rememberLazyGridState()
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing()),
            modifier = Modifier.height(calculatedSectionHeight),
            state = state,
            flingBehavior = rememberSnapFlingBehavior(
                lazyGridState = state,
                snapPosition = SnapPosition.Start
            )
        ) {
            item { Spacer(modifier = Modifier.width(Dimensions.contentPadding())) }
            items(items) { item ->
                AlbumCard(
                    item = item,
                    modifier = Modifier.width(calculatedItemWidth),
                    imageSize = calculatedItemWidth,
                    onClick = { onItemClick(item) }
                )
            }
            item { Spacer(modifier = Modifier.width(Dimensions.contentPadding())) }
        }
    }
}

@Composable
fun HorizontalScrollableNewScreenSection(
    title: String? = null,
    @DrawableRes iconHeader: Int? = null,
    items: List<NewReleaseCard>,
    itemWidth: Dp? = null,
    sectionHeight: Dp? = null,
    onItemClick: (MusicItem) -> Unit = {}  // Add click handler
) {
    val windowSize = rememberWindowSize()

    val calculatedItemWidth = itemWidth ?: when (windowSize) {
        WindowSize.COMPACT -> 160.dp
        WindowSize.MEDIUM -> 180.dp
    }

    val calculatedSectionHeight = sectionHeight ?: (calculatedItemWidth + 80.dp)

    Column {
        if (title?.isNotEmpty() == true) {
            SectionTitle(title)
//            if (iconHeader == null) {
//                SectionTitle(title)
//            } else {
//                SectionTitleWithIcon(title, iconHeader)
//            }
        }
        LazyHorizontalGrid(
            rows = GridCells.Fixed(1),
            contentPadding = PaddingValues(horizontal = Dimensions.contentPadding()),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing()),
            modifier = Modifier.height(calculatedSectionHeight)
        ) {
            items(items) { item ->
                AlbumCardNewScreen(
                    type = item.type,
                    item = item.musicItem,
                    modifier = Modifier.width(calculatedItemWidth),
                    imageSize = calculatedItemWidth,
                    onClick = { onItemClick(item.musicItem) }
                )
            }
        }
    }
}

@Composable
fun HorizontalScrollableNewScreenSection2(
    title: String? = null,
    @DrawableRes iconHeader: Int? = null,
    items: List<List<MusicItem>>,
    itemWidth: Dp? = null,
    sectionHeight: Dp? = null,
    onItemClick: (MusicItem) -> Unit = {}  // Add click handler
) {
    val windowSize = rememberWindowSize()

    val calculatedItemWidth = itemWidth ?: when (windowSize) {
        WindowSize.COMPACT -> 160.dp
        WindowSize.MEDIUM -> 180.dp
    }

    val calculatedSectionHeight = sectionHeight ?: (calculatedItemWidth + 80.dp)

    Column(modifier = Modifier.padding(top = 20.dp)) {
        SectionTitle(title ?: "")
//        if (title?.isNotEmpty() == true) {
//            if (iconHeader == null) {
//                SectionTitle(title)
//            } else {
//                SectionTitleWithIcon(title, iconHeader)
//            }
//        }
        LazyHorizontalGrid(
            state = rememberLazyGridState(),
            rows = GridCells.Fixed(1),
            contentPadding = PaddingValues(horizontal = Dimensions.contentPadding()),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing()),
            modifier = Modifier
                .height(calculatedSectionHeight)
                .padding(top = 14.dp)
        ) {
            items(items) { songList ->
                Column(
                    modifier = Modifier.width(itemWidth!! + 10.dp)
                ) {
                    songList.forEach {
                        SongInPlaylist(it, onClick = {onItemClick(it)})
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalScrollableNewScreenSection3(
    title: String? = null,
    @DrawableRes iconHeader: Int? = null,
    items: List<MusicItem>,
    itemWidth: Dp? = null,
    sectionHeight: Dp? = null,
    onItemClick: (MusicItem) -> Unit = {}  // Add click handler
) {
    val windowSize = rememberWindowSize()

    val calculatedItemWidth = itemWidth ?: when (windowSize) {
        WindowSize.COMPACT -> 160.dp
        WindowSize.MEDIUM -> 180.dp
    }

    val calculatedSectionHeight = sectionHeight ?: (calculatedItemWidth + 80.dp)

    Column(modifier = Modifier.padding(top = 20.dp)) {
        if (title?.isNotEmpty() == true) {
            SectionTitle(title)
//            if (iconHeader == null) {
//                SectionTitle(title)
//            } else {
//                SectionTitleWithIcon(title, iconHeader)
//            }
        }
        LazyHorizontalGrid(
            state = rememberLazyGridState(),
            rows = GridCells.Fixed(1),
            contentPadding = PaddingValues(horizontal = Dimensions.contentPadding()),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing()),
            modifier = Modifier
                .height(calculatedSectionHeight)
                .padding(top = 14.dp)
        ) {
            items(items) { item ->
                Column(
                    modifier = Modifier.width(itemWidth!! + 10.dp)
                ) {
                    AlbumCardNewScreenSelectionType3(
                        item = item,
                        modifier = Modifier.width(calculatedItemWidth),
                        imageSize = calculatedItemWidth,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalScrollableNewScreenSection4(
    title: String? = null,
    @DrawableRes iconHeader: Int? = null,
    items: List<List<MusicItem>>,
    itemWidth: Dp? = null,
    sectionHeight: Dp? = null,
    onItemClick: (MusicItem) -> Unit = {}  // Add click handler
) {
    val windowSize = rememberWindowSize()

    val calculatedItemWidth = itemWidth ?: when (windowSize) {
        WindowSize.COMPACT -> 160.dp
        WindowSize.MEDIUM -> 180.dp
    }

    val calculatedSectionHeight = sectionHeight ?: (calculatedItemWidth + 80.dp)

    Column(modifier = Modifier.padding(top = 20.dp)) {
        if (title?.isNotEmpty() == true) {
            if (iconHeader == null) {
                SectionTitle(title)
            } else {
                SectionTitleWithIcon(title, iconHeader)
            }
        }
        LazyHorizontalGrid(
            state = rememberLazyGridState(),
            rows = GridCells.Fixed(1),
            contentPadding = PaddingValues(horizontal = Dimensions.contentPadding()),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.itemSpacing()),
            modifier = Modifier
                .height(calculatedSectionHeight)
                .padding(top = 14.dp)
        ) {

            items(items) { songList ->
                Column(
                    modifier = Modifier.width(itemWidth!! + 10.dp)
                ) {
                    songList.forEach { item ->
                        val paddingTop = if (songList.first() == item) {
                            0.dp
                        } else {
                            10.dp
                        }
                        AlbumCardNewScreenSelectionType3(item = item,
                            modifier = Modifier
                                .width(calculatedItemWidth)
                                .padding(top = paddingTop),
                            imageSize = calculatedItemWidth,
                            onClick = { onItemClick(item) })
                    }
                }
            }
        }
    }
}

//này là tựa đề cho danh sách ở trên
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(
            start = Dimensions.contentPadding(),
            end = Dimensions.contentPadding(),
            top = 16.dp,
            bottom = 8.dp
        )
    )
}

@Composable
fun SectionTitleWithIcon(title: String, icon: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(
                start = Dimensions.contentPadding()
            )
        )
        Icon(
            painter = painterResource(icon),
            contentDescription = "Detail type",
            tint = Color.Gray
        )
    }
}

@Composable
fun AlbumCard(
    item: MusicItem,
    modifier: Modifier = Modifier,
    imageSize: Dp,
    onClick: () -> Unit = {}
) {
    val painter = rememberAsyncImagePainter(item.imageUrl);
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Image(
            painter = painterResource(item.imageRId ?: R.drawable.tiumarksvg),
            contentDescription = "Album art for ${item.title}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF282828))
        )
//        AsyncImage(
//            model = item.imageUrl,
//            fallback = painter,
//            contentDescription = "Album art for ${item.title}",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .size(imageSize)
//                .clip(RoundedCornerShape(12.dp))
//                .background(Color(0xFF282828))
//        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.title ?: "",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.artist ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AlbumCardNewScreen(
    type : String,
    item: MusicItem,
    modifier: Modifier = Modifier,
    imageSize: Dp,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Text(
            text = type,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.artist,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "Album art for ${item.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(imageSize)
                    .height(imageSize - 60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .width(imageSize - 44.dp)
                        .align(Alignment.CenterVertically),
                    text = item.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = "Album art for ${item.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF282828))
                )
            }

        }
    }
}

@Composable
fun AlbumCardNewScreenSelectionType3(
    item: MusicItem,
    modifier: Modifier = Modifier,
    imageSize: Dp,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = "Album art for ${item.title}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(imageSize)
                .height(imageSize)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF282828))
        )

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.artist,
            style = MaterialTheme.typography.bodyMedium,
            color = ArtistNameColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AlbumCardNewScreenListVertical(
    items: List<MusicItem>,
    modifier: Modifier = Modifier,
    imageSize: Dp,
    onClick: () -> Unit = {}
) {

}

//cái này đặt ngoài navHost
@Composable
fun NowPlayingSheet(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel,
    visualizerViewModel: VisualizerViewModel,
    ytmusicViewModel: YtmusicViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val dragProgress = remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    val ytPlayerHelper by playerViewModel.ytViewModel.ytHelper.collectAsState()
    val shouldExpand by playerViewModel.shouldExpand.collectAsState()
    val musicItem by playerViewModel.musicItem.collectAsState()
    // Su dung de check user dang seek hay khong
    // Set true tai onSeek khi user dang keo slider
    // Set false khi !isPlaying khi chuyen tu state paused sang playing
    // Still very retarded way to do this
    var isSeeking by remember { mutableStateOf(false) }

    val springSpec = SpringSpec<Float>(
        dampingRatio = 0.8f,
        stiffness = Spring.StiffnessLow
    )

    val progress by animateFloatAsState(
        targetValue = dragProgress.value,
        animationSpec = springSpec,
        label = "Sheet Progress"
    )
    
    // Update the expanded state based on progress
    LaunchedEffect(progress) {
        playerViewModel.setExpanded(progress > 0.5f)
    }

    LaunchedEffect(shouldExpand) {
        if (shouldExpand == 1) {
            dragProgress.value = 1.0f;
            playerViewModel.setShouldExpand(0);
            playerViewModel.setExpanded(true);
        }
        if (shouldExpand == -1) {
            dragProgress.value = 0.0f;
            playerViewModel.setShouldExpand(0);
            playerViewModel.setExpanded(false);
        }
    }

    val maxHeight = LocalConfiguration.current.screenHeightDp.dp
    val minHeight = 80.dp
    val height = lerp(minHeight, maxHeight, progress)

    val dragState = rememberDraggableState { delta ->
        val newProgress = (dragProgress.value - delta / maxHeight.value).coerceIn(0f, 1f)
        dragProgress.value = newProgress
    }


    val viewModel: MediaViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTrackState by viewModel.currentPlayingIndex.collectAsStateWithLifecycle()
    val isPlayingState by viewModel.isPlaying.collectAsStateWithLifecycle()
    val totalDurationState by viewModel.totalDurationInMS.collectAsStateWithLifecycle()
    var currentPositionState by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isPlayingState) {
        while (isPlayingState) {
            currentPositionState = viewModel.player.currentPosition
            delay(1.seconds)
        }
    }

    AudioPlayerView(viewModel)
//    when (uiState) {
//        PlayerUIState.Loading, PlayerUIState.Initial -> {
//
//        }
//
//        is PlayerUIState.Tracks -> {
//            Column(modifier = Modifier.fillMaxSize()) {
//                AudioPlayerView(viewModel)
//            }
//        }
//    }

    YoutubeView(
        youtubeVideoId = musicItem.videoId,
        youtubeViewModel = playerViewModel.ytViewModel,
        onSecond = { ytPlayer, second ->
            if (!isSeeking)
                playerViewModel.setCurrentTime(second);
        },
        onDurationLoaded = { ytPlayer, dur ->
            playerViewModel.setDuration(dur);
        },
        onState =  { ytPlayer, state ->
            when(state) {
                PlayerConstants.PlayerState.PLAYING -> {
                    if (!playerViewModel.isPlaying.value)
                        isSeeking = false;
                    playerViewModel.setPlaying(true);
                }
                PlayerConstants.PlayerState.PAUSED, PlayerConstants.PlayerState.ENDED -> {
                    playerViewModel.setPlaying(false)
                };
                PlayerConstants.PlayerState.BUFFERING -> {
                    playerViewModel.setPlaying(false);
                } // Set Loading
                else -> {
                    playerViewModel.setPlaying(false);
                    // Set Loading
                }
            }
        }
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .draggable(
                state = dragState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity ->
                    val targetValue = if (dragProgress.value > 0.5f || velocity < -500f) 1f else 0f
                    scope.launch {
                        animate(
                            initialValue = dragProgress.value,
                            targetValue = targetValue,
                            animationSpec = springSpec
                        ) { value, _ ->
                            dragProgress.value = value
                        }
                    }
                }
            )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
            ),
            color = Color(0xFF282828)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Crossfade(
                    targetState = progress > 0.5f,
                    label = "Player State"
                ) { isExpanded ->
                    if (!isExpanded) {
                        MiniPlayer(
                            musicItem = musicItem,
                            isPlaying = playerViewModel.isPlaying.value,
                            onPlayPauseClick = {
                                if (playerViewModel.isPlaying.value)
                                    ytPlayerHelper.pause();
                                else
                                    ytPlayerHelper.play();
                            },
                        )
                    } else {
                        ExpandedPlayer(
                            musicItem = musicItem,
                            isPlaying = playerViewModel.isPlaying.value,
                            duration = playerViewModel.duration.value,
                            currentTime = playerViewModel.currentTime.value,
                            onPlayPauseClick = {
                                if (playerViewModel.isPlaying.value)
                                    ytPlayerHelper.pause();
                                else
                                    ytPlayerHelper.play();
                           },
                            onSeek = { newPosition ->
                                isSeeking = true;
                            },
                            onSeekFinished = { newPosition ->
                                playerViewModel.setPlaying(false);
                                playerViewModel.setCurrentTime(newPosition);
                                ytPlayerHelper.seekTo(newPosition);
                            },
                            onChangeSong = { isNextSong ->
                                if (!isNextSong && ytPlayerHelper.currentSecond >= 5) {
                                    ytPlayerHelper.seekTo(0f);
                                }
                                else {
                                    playerViewModel.changeSong(isNextSong, MainActivity.applicationContext)
                                }
                            },
                            visualizerViewModel = visualizerViewModel,
                            playerViewModel = playerViewModel,
                            ytmusicViewModel = ytmusicViewModel,
                            navController = navController
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 64.dp)
                        .width(64.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray.copy(alpha = progress))
                )
            }
        }
    }
}

@Composable
private fun MiniPlayer(
    musicItem: MusicItem,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album art and info
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = getYoutubeSmallThumbnail(musicItem.videoId),
                contentDescription = "song image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF404040))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = musicItem.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    color = Color.White,
                    modifier = Modifier
                        .basicMarquee(
                            animationMode = MarqueeAnimationMode.Immediately,
                            iterations = Int.MAX_VALUE,
                            velocity = 50.dp,
                            initialDelayMillis = 50
                        )
                )
                Text(
                    text = musicItem.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF404040), CircleShape)
        ) {
            Icon(
                painter = painterResource( if (isPlaying) R.drawable.pause_solid else R.drawable.play_solid),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(), // Chiếm toàn bộ màn hình
        contentAlignment = Alignment.Center // Căn giữa nội dung trong Box
    ) {
        CircularProgressIndicator() // Hiển thị spinner
    }
}

/*
để fun chỉnh volume ra ngoài để sync với system, dùng observe để sync với system.
chỉnh volume ko đc mượt tại system có volume theo từng nắc từ 0 đến 10, nên có 10 nắc
(này chịu, nhma cái animation vjp pro này phân tán sự chú ý problem này r =)))))
 */

private fun adjustVolume(audioManager: AudioManager, newVolume: Float, maxVolume: Float) {
    val adjustedVolume = (newVolume * maxVolume).coerceIn(0f, maxVolume).toInt()
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, adjustedVolume, 0)
}


