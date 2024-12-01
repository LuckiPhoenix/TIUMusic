package com.example.TIUMusic.Libs.YoutubeLib

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.IntentSender.OnFinished
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
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
import com.example.TIUMusic.Libs.YoutubeLib.models.LRCLIBObject2
import com.example.TIUMusic.Libs.YoutubeLib.models.Line2
import com.example.TIUMusic.Libs.YoutubeLib.models.Lyrics2
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import io.ktor.client.call.body
import okhttp3.internal.notify

fun ensurePlayerNotificationPermissionAllowed(
    activity : ComponentActivity,
    onPermissionAccepted: () -> Unit,
    onFinished: () -> Unit
) {
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
            if (YoutubeSettings.NotificationEnabled)
                onPermissionAccepted();
            onFinished();
        }
    when {
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED -> {
            YoutubeSettings.NotificationEnabled = true;
            onPermissionAccepted();
            onFinished();
            // You can use the API that requires the permission.
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.POST_NOTIFICATIONS) -> {
            YoutubeSettings.NotificationEnabled = false;
            onFinished();
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
    youtubeViewModel: YoutubeViewModel
) {
    var ytPlayerView : YouTubePlayerView? by remember { mutableStateOf(null) }
    val ytPlayerHelper by youtubeViewModel.ytHelper.collectAsState()
    val mediaSession by youtubeViewModel.mediaSession.collectAsState()
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
                        youtubeViewModel.updateYoutubePlayer(youTubePlayer);
                        youTubePlayer.loadVideo(youtubeVideoId, 0f);
                    }

                    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                        onDurationLoaded(youTubePlayer, duration);
                        if (mediaSession != null && (!mediaSession!!.isActive || youtubeViewModel.reloadDuration))
                        {
                            youtubeViewModel.reloadDuration = false;
                            youtubeViewModel.updateVideoDuration(durationMs = duration.toLong() * 1000L);
                            youtubeViewModel.setMediaSessionActive(true);
                            // println("Playing");
                            youtubeViewModel.updatePlaybackState(
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
                        youtubeViewModel.updateVideoDuration(ytPlayerHelper.ytVideoTracker.videoDuration.toLong() * 1000L);
                        youtubeViewModel.updatePlaybackState(
                            state = state,
                            position = ytPlayerHelper.seekToTime.toLong() * 1000L,
                            playbackSpeed = 1.0f
                        );
                    }
                });
            }
        }
    )
}
fun getYoutubeHDThumbnail(videoId: String) : String {
    return "https://img.youtube.com/vi/$videoId/maxresdefault.jpg";
}

fun parseSyncedLyrics(syncedLyrics : String) : List<Line2> {
    var line2s : MutableList<Line2> = mutableListOf();
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
        line2s.add(Line2(startSeconds = seconds, words = it.substring(i + 1)))
    };

    return line2s;
}

fun parsePlainLyrics(plainLyrics : String) : List<Line2> {
    var line2s : MutableList<Line2> = mutableListOf();
    plainLyrics.split('\n').forEach{ it ->
        line2s.add(Line2(startSeconds = 0f, words = it));
    };
    return line2s;
}

suspend fun getLyrics(ytMusic : Ytmusic, track : String, artist : String) : Lyrics2 {
    var line2s : List<Line2> = emptyList();
    var isSynced = false;
    val lyricsList = ytMusic.searchLrclibLyrics("it's not litter if you bin it", "Niko B").body<List<LRCLIBObject2>>();
    if (lyricsList.isNotEmpty()) {
        val lrclibObj = lyricsList.first();
        if (lrclibObj.syncedLyrics != null) {
            lrclibObj.syncedLyrics.let { line2s = parseSyncedLyrics(it) };
            isSynced = true;
        }
        else if (lrclibObj.plainLyrics != null) {
            lrclibObj.plainLyrics.let { line2s = parsePlainLyrics(it) };
            isSynced = false;
        }
    }
    return Lyrics2(line2s = line2s, isSynced = true);
}