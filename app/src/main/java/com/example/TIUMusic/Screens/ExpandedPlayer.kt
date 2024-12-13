package com.example.TIUMusic.Screens

import android.content.Context
import android.database.ContentObserver
import android.graphics.Bitmap
import android.media.AudioManager
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.toBitmap
import com.example.TIUMusic.Libs.MediaPlayer.AudioPlayerView
import com.example.TIUMusic.Libs.MediaPlayer.MediaViewModel
import com.example.TIUMusic.Libs.MediaPlayer.PlayerUIState
import com.example.TIUMusic.Libs.Visualizer.VisualizerCircleRGB
import com.example.TIUMusic.Libs.Visualizer.VisualizerViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Login.Playlist
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.ui.theme.PrimaryColor
import com.example.TIUMusic.ui.theme.SecondaryColor
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

var showBottomSheet : Boolean = false
var showSleepTimerSheet: Boolean = false
var showUserPlaylistSheet: Boolean = false
@OptIn(ExperimentalAnimationApi::class)
@Composable
public fun ExpandedPlayer(
    musicItem: MusicItem,
    isPlaying: Boolean,
    currentTime: Float,
    duration : Float,
    onPlayPauseClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
    onChangeSong: (Boolean) -> Unit,
    visualizerViewModel: VisualizerViewModel,
    playerViewModel: PlayerViewModel,
    ytmusicViewModel: YtmusicViewModel,
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 50000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    var albumArt : Bitmap? by remember { mutableStateOf(null) }
    var avgColor : Color by remember { mutableStateOf(Color.Transparent) }
    val syncedLine by playerViewModel.syncedLine.collectAsState()
    val currentUser by userViewModel.currentUser.observeAsState()

    LaunchedEffect(albumArt) {
        if (albumArt != null) {
            avgColor = Color(albumArt!!.getPixel(0, 0));
            Log.d("Player", avgColor.toString());
            avgColor = avgColor.copy(
                alpha = 1.0f,
                red = min(1.0f, avgColor.red * 1.5f),
                green = min(1.0f, avgColor.green * 1.5f),
                blue = min(1.0f, avgColor.blue * 1.5f)
            )
        }
        userViewModel.getCurrentUser()
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier =
        Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0f to avgColor.copy(alpha = 0.8f),
                    0.6f to avgColor.copy(alpha = 0f),
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            // Album art
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxSize()
                    .weight(1f)

            ) {
                VisualizerCircleRGB(
                    visualizerViewModel = visualizerViewModel,
                    radius = 330.dp.value,
                    lineHeight = 550.dp.value,
                )
                AsyncImage(
                    model = musicItem.imageUrl,
                    contentDescription = "Song Image",
                    contentScale = ContentScale.FillHeight,
                    onSuccess = { result ->
                        albumArt =
                            result.result.image.toBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    },
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(140.dp))
                        .background(Color(0xFF404040))
                        .graphicsLayer(rotationZ = rotation)
                )
            }

            Text(
                text = syncedLine.words, //lyric here
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .height(48.dp)
                    .fillMaxWidth()
            )


            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                // Title and artist
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier
                        .padding(start = 16.dp)
                        .widthIn(max = 250.dp)
                    ) {
                        Text(
                            text = musicItem.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = musicItem.artist,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {showBottomSheet = !showBottomSheet},
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ellipsis_solid),
                            contentDescription = "More",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                PlaybackControls(
                    isPlaying = isPlaying,
                    onPlayPauseClick = onPlayPauseClick,
                    currentTime = currentTime,
                    duration = duration,
                    onSeek = onSeek,
                    onSeekFinished = onSeekFinished,
                    onChangeSong = { isNextSong -> onChangeSong(isNextSong) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                VolumeControls()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        if(showBottomSheet == true){
            PlayMenuBottomSheet(
                navController,
                musicItem = musicItem,
                playerViewModel = playerViewModel,
                onRepeatClick = {
                    playerViewModel.setLoop(!playerViewModel.loop.value);
                })
        }
        if(showSleepTimerSheet == true){
            SleepTimerSheet(
                onDismissRequest = {
                    showSleepTimerSheet = false
                },
                onTimerStart = {
                    time -> startTimer(time)
                }
            )
        }
        if(showUserPlaylistSheet == true){
            currentUser?.let {
                UserPlaylistBottomSheet(
                    onDismissRequest = { showUserPlaylistSheet = false},
                    it.playlists,
                    onAdding = {id ->
                        userViewModel.addSongToPlaylist(id, musicItem)
                        Log.d("LogNav", "Add musicId : ${musicItem.videoId} to $id")
                    }
                )
            }
        }
    }
}
fun startTimer(duration: Duration) {
    //sleep timer
    showSleepTimerSheet = false
    var remainingTime = 0L
    var isTimerRunning = false

    isTimerRunning = true
    remainingTime = duration.inWholeMilliseconds

    object : CountDownTimer(duration.inWholeMilliseconds, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            remainingTime = millisUntilFinished
        }

        override fun onFinish() {
            isTimerRunning = false
            exitProcess(0)
        }
    }.start()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayMenuBottomSheet(
    navController: NavController,
    onRepeatClick: () -> Unit,
    musicItem: MusicItem,
    playerViewModel: PlayerViewModel
){
    var isFavorite by remember { mutableStateOf(false) }
    ModalBottomSheet(
        containerColor = Color.Black,
        shape = RoundedCornerShape(0.dp),
        onDismissRequest = {
            showBottomSheet = false
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = musicItem.imageUrl,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF282828))
            )
            Column {
                Text(
                    text = "My Playlist", //Khi nao tao chuc nang thi sua thanh musicItem.title
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )
                Text(
                    text = "Artist",
                    color = Color(0xFFfc3c44),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
        HorizontalDivider(
            thickness = 2.dp,
            color = Color(color = 0xFF878787),
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    isFavorite = !isFavorite
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    if (isFavorite) R.drawable.star_solid else R.drawable.star_regular
                ),
                contentDescription = "Favorite",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Favorite",
                color = Color.White
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onRepeatClick(); },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.repeat_solid),
                contentDescription = "Repeat",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Repeat",
                color = Color.White
            )
            if(playerViewModel.loop.value) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.check_solid),
                    contentDescription = "Repeat",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 16.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    showUserPlaylistSheet = !showUserPlaylistSheet
                    showBottomSheet = !showBottomSheet
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.plus_solid),
                contentDescription = "Add to Playlist",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Add to Playlist",
                color = Color.White
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.list_plus),
                contentDescription = "Play Next",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Play Next",
                color = Color.White
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { navController.navigate("artist/${musicItem.browseId}") },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.image_portrait_solid),
                contentDescription = "Artist",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Artist",
                color = Color.White
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    showSleepTimerSheet = !showSleepTimerSheet
                    showBottomSheet = !showBottomSheet
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.moon_solid),
                contentDescription = "Sleep timer",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Set a sleep timer",
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimerSheet(
    onDismissRequest: () -> Unit,
    onTimerStart: (Duration) -> Unit
) {
    ModalBottomSheet(
        containerColor = Color.Black,
        onDismissRequest = onDismissRequest
    ) {
        Text(
            color = Color.White,
            text = "Turn off",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    onDismissRequest()
                }
        )
        Text(
            color = Color.White,
            text = "After 15 minutes",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    onTimerStart(15.toDuration(DurationUnit.MINUTES))
                    onDismissRequest()
                    showBottomSheet = false
                }
        )
        Text(
            color = Color.White,
            text = "After 30 minutes",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    onTimerStart(30.toDuration(DurationUnit.MINUTES))
                    onDismissRequest()
                    showBottomSheet = false
                }
        )
        Text(
            color = Color.White,
            text = "After 1 hour",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    onTimerStart(1.toDuration(DurationUnit.HOURS))
                    onDismissRequest()
                    showBottomSheet = false
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPlaylistBottomSheet(
    onDismissRequest: () -> Unit,
    playlists: MutableList<Playlist>,
    onAdding: (id: String) -> Unit
    ) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        playlists.forEach { item ->
            Text(
                text = item.title,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable {
                        onDismissRequest()
                        // + Add to playlist
                        onAdding(item.id)
                        Toast
                            .makeText(context, "Adding to ${item.title}", Toast.LENGTH_SHORT)
                            .show()
                        showBottomSheet = false
                    }
            )
        }
        Text(
            text = "(+) Create new Playlist",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable {
                    onDismissRequest()
                    showBottomSheet = false
                }
        )
    }
}


@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    currentTime: Float,
    duration: Float,
    onPlayPauseClick: () -> Unit,
    onChangeSong : (Boolean) -> Unit,
    onSeek: (Float) -> Unit, // User changes the time
    onSeekFinished: (Float) -> Unit,
) {
    // Local state for handling slider interactions
    var sliderPosition by remember { mutableStateOf(currentTime) }
    var isSeeking by remember { mutableStateOf(false) }

    if (!isSeeking)
        sliderPosition = currentTime; // Really stupid

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { newPosition ->
                isSeeking = true;
                sliderPosition = newPosition
                // Notify the wrapper about the change
                // to disable slider sync when seeking
                onSeek(sliderPosition)
            },
            onValueChangeFinished = {
                isSeeking = false;
                // Change here
                onSeekFinished(sliderPosition)
            },
            valueRange = 0f..duration,
            colors = SliderColors(
                thumbColor = PrimaryColor,
                activeTrackColor = SecondaryColor,
                activeTickColor = SecondaryColor,
                inactiveTrackColor = Color(0x33FFFFFF),
                inactiveTickColor = Color(0x33FFFFFF),
                disabledThumbColor = PrimaryColor,
                disabledActiveTrackColor = SecondaryColor,
                disabledActiveTickColor = SecondaryColor,
                disabledInactiveTrackColor = Color(0x33FFFFFF),
                disabledInactiveTickColor = Color(0x33FFFFFF)
            ),
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
            Text(formatTime(sliderPosition), color = Color.Gray)
            Text(formatTime(duration), color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onChangeSong(false) }) {
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
                    painter = painterResource(
                        if (isPlaying) R.drawable.pause_solid else R.drawable.play_solid
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = { onChangeSong(true) }) {
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
            Settings.System.CONTENT_URI,
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

@Composable
fun formatTime(timeInSeconds: Float): String {
    val minutes = (timeInSeconds / 60).toInt()
    val seconds = (timeInSeconds % 60).toInt()
    return String.format("%d:%02d", minutes, seconds)
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
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "offsetAnimation"
    )

    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(
            stiffness = Spring.StiffnessVeryLow,
            dampingRatio = Spring.DampingRatioNoBouncy
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