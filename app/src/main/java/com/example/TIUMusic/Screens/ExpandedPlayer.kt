package com.example.TIUMusic.Screens

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeMetadata
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeView
import com.example.TIUMusic.R
import com.example.TIUMusic.ui.theme.PrimaryColor
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import kotlin.math.roundToInt

@Composable
public fun ExpandedPlayer(
    isPlaying: Boolean,
    currentTime: Float,
    duration : Float,
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
            currentTime = currentTime,
            duration = duration,
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

