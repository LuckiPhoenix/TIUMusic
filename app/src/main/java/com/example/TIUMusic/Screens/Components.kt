package com.example.TIUMusic.Screens

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.ui.theme.BackgroundColor
import com.example.TIUMusic.ui.theme.PrimaryColor
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import com.example.TIUMusic.R

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
    playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current
    val dragProgress = remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

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
                            onPlayPauseClick = { playerViewModel.setPlaying(!playerViewModel.isPlaying.value) }
                        )
                    } else {
                        ExpandedPlayer(
                            isPlaying = playerViewModel.isPlaying.value,
                            onPlayPauseClick = { playerViewModel.setPlaying(!playerViewModel.isPlaying.value) }
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

@Composable
private fun ExpandedPlayer(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        // Album art
        AsyncImage(
            model = "",
            contentDescription = "Song Image",
            modifier = Modifier
                .size(320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF404040))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Title and artist
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = "Song Title",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Artist Name",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }


        PlaybackControls(
            isPlaying = isPlaying,
            onPlayPauseClick = onPlayPauseClick,
            currentTime = 0f,
            duration = 10f,
            onSeek = {}
        )

        Spacer(modifier = Modifier.height(32.dp))

        VolumeControls()
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    currentTime: Float,
    duration: Float,
    onPlayPauseClick: () -> Unit,
    onSeek: (Float) -> Unit // user chỉnh time
) {
    Column {
        Slider(
            value = currentTime,
            onValueChange = onSeek,
            valueRange = 0f..duration,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // Time indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(currentTime), color = Color.Gray)
            Text(formatTime(duration), color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* quay về stack trước */ }) {
                Icon(
                    painter = painterResource(R.drawable.prev_song),
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFF404040), CircleShape)
            ) {
                Icon(
                    painter = painterResource( if (isPlaying) R.drawable.pause else R.drawable.play_solid),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = { /* nhạc tiếp theo */ }) {
                Icon(
                    painter = painterResource(R.drawable.next_song),
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun formatTime(timeInSeconds: Float): String {
    val minutes = (timeInSeconds / 60).toInt()
    val seconds = (timeInSeconds % 60).toInt()
    return String.format("%d:%02d", minutes, seconds)
}

@Composable
fun VolumeControls(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()

    //2 cái tại volume theo nấc là OS, cái visual trick user nó mượt
    var visualVolume by remember { mutableStateOf(0f) }
    var systemVolume by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val initial = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maxVolume
        visualVolume = initial
        systemVolume = initial
    }

    DisposableEffect(context) {
        val volumeObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                val newVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maxVolume
                systemVolume = newVolume
                visualVolume = newVolume  // Update visual volume when system changes
            }
        }
        context.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            volumeObserver
        )

        onDispose {
            context.contentResolver.unregisterContentObserver(volumeObserver)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            val newVolume = (visualVolume - 0.1f).coerceIn(0f, 1f)
            visualVolume = newVolume
            adjustVolume(audioManager, newVolume, maxVolume)
        }) {
            Icon(
                painter = painterResource(R.drawable.volume_1),
                contentDescription = "Volume Down",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        LineSlider(
            value = visualVolume,
            onValueChange = { newVisualVolume ->
                visualVolume = newVisualVolume
                adjustVolume(audioManager, newVisualVolume, maxVolume)
            },
            valueRange = 0f..1f,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            steps = 0,
            thumbDisplay = { (it * 100).toInt().toString() }
        )

        IconButton(onClick = {
            val newVolume = (visualVolume + 0.1f).coerceIn(0f, 1f)
            visualVolume = newVolume
            adjustVolume(audioManager, newVolume, maxVolume)
        }) {
            Icon(
                painter = painterResource(R.drawable.volume_2),
                contentDescription = "Volume Up",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
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

val thumbSize = 48.dp // size cái nút âm lượng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    thumbDisplay: (Float) -> String = { "" },
) {
    val interaction = remember { MutableInteractionSource() }
    val isDragging by interaction.collectIsDraggedAsState()
    val density = LocalDensity.current

    val offsetHeight by animateFloatAsState(
        targetValue = with(density) { if (isDragging) 36.dp.toPx() else 0.dp.toPx() },
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "offsetAnimation"
    )

    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(
            stiffness = Spring.StiffnessVeryLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "animatedValue"
    )

    Slider(
        value = animatedValue,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        interactionSource = interaction,
        thumb = {},
        track = { sliderState ->
            val fraction by remember {
                derivedStateOf {
                    (animatedValue - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                }
            }

            var width by remember { mutableIntStateOf(0) }

            Box(
                Modifier
                    .clearAndSetSemantics { }
                    .height(thumbSize)
                    .fillMaxWidth()
                    .onSizeChanged { width = it.width },
            ) {
                Box(
                    Modifier
                        .zIndex(10f)
                        .align(Alignment.CenterStart)
                        .offset {
                            IntOffset(
                                x = lerp(
                                    start = -(thumbSize / 2).toPx(),
                                    end = width - (thumbSize / 2).toPx(),
                                    t = fraction
                                ).roundToInt(),
                                y = -offsetHeight.roundToInt(),
                            )
                        }
                        .size(thumbSize)
                        .padding(10.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                        )
                        .background(
                            color = PrimaryColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        thumbDisplay(animatedValue),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                val strokeColor = MaterialTheme.colorScheme.onSurface
                val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .drawWithCache {
                            onDrawBehind {
                                scale(
                                    scaleY = 1f,
                                    scaleX = if (isLtr) 1f else -1f
                                ) {
                                    drawSliderPath(
                                        fraction = fraction,
                                        offsetHeight = offsetHeight,
                                        color = strokeColor,
                                        steps = sliderState.steps
                                    )
                                }
                            }
                        }
                )
            }
        }
    )
}

fun DrawScope.drawSliderPath(
    fraction: Float,
    offsetHeight: Float,
    color: Color,
    steps: Int,
) {
    val path = Path()
    val activeWidth = size.width * fraction
    val midPointHeight = size.height / 2
    val curveHeight = midPointHeight - offsetHeight
    val beyondBounds = size.width * 2
    val ramp = 64.dp.toPx()

    path.moveTo(
        x = beyondBounds,
        y = midPointHeight
    )

    path.lineTo(
        x = activeWidth + ramp,
        y = midPointHeight
    )

    path.cubicTo(
        x1 = activeWidth + (ramp / 2),
        y1 = midPointHeight,
        x2 = activeWidth + (ramp / 2),
        y2 = curveHeight,
        x3 = activeWidth,
        y3 = curveHeight,
    )

    path.cubicTo(
        x1 = activeWidth - (ramp / 2),
        y1 = curveHeight,
        x2 = activeWidth - (ramp / 2),
        y2 = midPointHeight,
        x3 = activeWidth - ramp,
        y3 = midPointHeight
    )

    path.lineTo(
        x = -beyondBounds,
        y = midPointHeight
    )

    val variation = .1f

    path.lineTo(
        x = -beyondBounds,
        y = midPointHeight + variation
    )

    path.lineTo(
        x = activeWidth - ramp,
        y = midPointHeight + variation
    )

    path.cubicTo(
        x1 = activeWidth - (ramp / 2),
        y1 = midPointHeight + variation,
        x2 = activeWidth - (ramp / 2),
        y2 = curveHeight + variation,
        x3 = activeWidth,
        y3 = curveHeight + variation,
    )

    path.cubicTo(
        x1 = activeWidth + (ramp / 2),
        y1 = curveHeight + variation,
        x2 = activeWidth + (ramp / 2),
        y2 = midPointHeight + variation,
        x3 = activeWidth + ramp,
        y3 = midPointHeight + variation,
    )

    path.lineTo(
        x = beyondBounds,
        y = midPointHeight + variation
    )

    val exclude = Path().apply {
        addRect(Rect(-beyondBounds, -beyondBounds, 0f, beyondBounds))
        addRect(Rect(size.width, -beyondBounds, beyondBounds, beyondBounds))
    }

    val trimmedPath = Path()
    trimmedPath.op(path, exclude, PathOperation.Difference)

    val pathMeasure = PathMeasure()
    pathMeasure.setPath(trimmedPath, false)

    val graduations = steps + 1
    for (i in 0..graduations) {
        val pos = pathMeasure.getPosition((i / graduations.toFloat()) * pathMeasure.length / 2)
        val height = 10f
        when (i) {
            0, graduations -> drawCircle(
                color = color,
                radius = 10f,
                center = pos
            )
            else -> drawLine(
                strokeWidth = if (pos.x < activeWidth) 4f else 2f,
                color = color,
                start = pos + Offset(0f, height),
                end = pos + Offset(0f, -height),
            )
        }
    }

    clipRect(
        left = -beyondBounds,
        top = -beyondBounds,
        bottom = beyondBounds,
        right = activeWidth,
    ) {
        drawTrimmedPath(trimmedPath, color)
    }
    clipRect(
        left = activeWidth,
        top = -beyondBounds,
        bottom = beyondBounds,
        right = beyondBounds,
    ) {
        drawTrimmedPath(trimmedPath, color.copy(alpha = .2f))
    }
}

fun DrawScope.drawTrimmedPath(path: Path, color: Color) {
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = 10f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}

fun lerp(start: Float, end: Float, t: Float) = start + t * (end - start)
