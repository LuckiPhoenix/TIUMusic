package com.example.TIUMusic.Screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.PrimaryColor
import kotlinx.coroutines.launch
import androidx.compose.ui.res.painterResource
import com.example.TIUMusic.Libs.YoutubeLib.MediaNotificationSeek
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeMetadata
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeView
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeViewModel
import com.example.TIUMusic.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

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
                snapPosition = SnapPosition.End
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
fun AlbumCard(
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
                .size(imageSize)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF282828))
        )

        Spacer(modifier = Modifier.height(8.dp))

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
    }
}

//cái này đặt ngoài navHost
@Composable
fun NowPlayingSheet(
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel,
    youtubeViewModel: YoutubeViewModel,
) {
    val context = LocalContext.current
    val dragProgress = remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    val ytPlayerHelper by youtubeViewModel.ytHelper.collectAsState()
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

    val maxHeight = LocalConfiguration.current.screenHeightDp.dp
    val minHeight = 80.dp
    val height = lerp(minHeight, maxHeight, progress)

    val dragState = rememberDraggableState { delta ->
        val newProgress = (dragProgress.value - delta / maxHeight.value).coerceIn(0f, 1f)
        dragProgress.value = newProgress
    }

    YoutubeView(
        youtubeVideoId = "Zgd1corMdnk",
        youtubeMetadata = YoutubeMetadata(
            title = "it's not litter if you bin it",
            artist = "Niko B",
        ),
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
        },
        youtubeViewModel = youtubeViewModel
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
                            }
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF404040))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Song Title",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = "Artist Name",
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
                painter = painterResource( if (isPlaying) R.drawable.pause else R.drawable.play_solid),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}