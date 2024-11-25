package com.example.TIUMusic.Libs.YoutubeLib

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.TIUMusic.Libs.YoutubeLib.models.LRCLIBObject
import com.example.TIUMusic.Libs.YoutubeLib.models.Line
import com.example.TIUMusic.Libs.YoutubeLib.models.Lyrics
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import io.ktor.client.call.body

fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is not in the Support Library.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "TIUMusic"
        val descriptionText = "Music Player"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Notification.CATEGORY_MESSAGE, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun ensurePlayerNotificationPermissionAllowed(activity : ComponentActivity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
        return;

    val requestPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                YoutubeSettings.NotificationEnabled = true;
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                YoutubeSettings.NotificationEnabled = false;
            }
        }
    when {
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED -> {
            YoutubeSettings.NotificationEnabled = true;
            // You can use the API that requires the permission.
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.POST_NOTIFICATIONS) -> {
            YoutubeSettings.NotificationEnabled = false;
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
        }
        else -> {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}


@Composable
fun YoutubeView(
    youtubeVideoId: String,
    youtubeMetadata: YoutubeMetadata,
    onSecond: (YouTubePlayer, Float) -> Unit,
    onDurationLoaded: (YouTubePlayer, Float) -> Unit,
    onState: (YouTubePlayer, PlayerConstants.PlayerState) -> Unit,
    ytHelper : YoutubeHelper
) {
    var ytPlayerView : YouTubePlayerView? by remember { mutableStateOf(null) }
    DisposableEffect(LocalContext.current) {
        onDispose {
            ytPlayerView?.release();
        }
    }
    AndroidView(
        modifier = Modifier.size(0.dp),
        factory = { context ->
            YouTubePlayerView(context = context).apply {
                ytPlayerView = this;
                //lifecycleOwner.lifecycle.addObserver(this);
                enableBackgroundPlayback(true);

                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        ytHelper.updateYoutubePlayer(youTubePlayer);
                        youTubePlayer.loadVideo(youtubeVideoId, 0f);
                    }

                    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                        onDurationLoaded(youTubePlayer, duration);
                        if (ytHelper.mediaSession != null && !ytHelper.mediaSession!!.isActive)
                        {
                            ytHelper.updateMediaMetadata(
                                metadata = youtubeMetadata,
                                durationMs = duration.toLong() * 1000L
                            );
                            ytHelper.setMediaSessionActive(true);
                            // println("Playing");
                            ytHelper.updatePlaybackState(
                                state = PlayerConstants.PlayerState.PLAYING,
                                position = 0,
                                playbackSpeed = 1.0f
                            );
                        }
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        onSecond(youTubePlayer, second);
                    }

                    override fun onStateChange(
                        youTubePlayer: YouTubePlayer,
                        state: PlayerConstants.PlayerState
                    ) {
                        onState(youTubePlayer, state);
                        if (state == PlayerConstants.PlayerState.UNSTARTED)
                            return;
                        ytHelper.updatePlaybackState(
                            state = state,
                            position = ytHelper.ytPlayerHelper.seekToTime.toLong() * 1000L,
                            playbackSpeed = 1.0f
                        );
                    }
                });
            }
        }
    )
}

fun parseSyncedLyrics(syncedLyrics : String) : List<Line> {
    var lines : MutableList<Line> = mutableListOf();
    syncedLyrics.split('\n').forEach { it ->
        // retarded certified
        var i : Int = 1;
        var seconds : Float = 0f;
        var multiplierCount : Int = 0;
        val multipliers : Array<Float> = arrayOf(60f, 1f, 0.01f);
        while (i < it.length && multiplierCount < 3) // Khong biet su dung regex belike
        {
            var num : String = "";
            while (i < it.length &&
                (it[i] != ':' && it[i] != '.' && it[i] != ']'))
            {
                num = num + it[i];
                i++;
            }
            seconds += num.toFloatOrNull()?.times(multipliers[multiplierCount]) ?: 0f;
            multiplierCount++;
            i++;
        }
        lines.add(Line(startSeconds = seconds, words = it.substring(i + 1)))
    };

    return lines;
}

fun parsePlainLyrics(plainLyrics : String) : List<Line> {
    var lines : MutableList<Line> = mutableListOf();
    plainLyrics.split('\n').forEach{ it ->
        lines.add(Line(startSeconds = 0f, words = it));
    };
    return lines;
}

suspend fun getLyrics(ytMusic : Ytmusic, track : String, artist : String) : Lyrics {
    var lines : List<Line> = emptyList();
    var isSynced = false;
    val lyricsList = ytMusic.searchLrclibLyrics("it's not litter if you bin it", "Niko B").body<List<LRCLIBObject>>();
    if (lyricsList.isNotEmpty()) {
        val lrclibObj = lyricsList.first();
        if (lrclibObj.syncedLyrics != null) {
            lrclibObj.syncedLyrics.let { lines = parseSyncedLyrics(it) };
            isSynced = true;
        }
        else if (lrclibObj.plainLyrics != null) {
            lrclibObj.plainLyrics.let { lines = parsePlainLyrics(it) };
            isSynced = false;
        }
    }
    return Lyrics(lines = lines, isSynced = true);
}