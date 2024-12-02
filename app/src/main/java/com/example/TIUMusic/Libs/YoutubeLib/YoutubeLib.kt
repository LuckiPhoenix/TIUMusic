package com.example.TIUMusic.Libs.YoutubeLib

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.TIUMusic.Libs.YoutubeLib.models.LRCLIBObject
import com.example.TIUMusic.Libs.YoutubeLib.models.Line
import com.example.TIUMusic.Libs.YoutubeLib.models.Lyrics
import com.example.TIUMusic.MainActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import io.ktor.client.call.body
import kotlin.math.abs

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
    onSecond: (YouTubePlayer, Float) -> Unit,
    onDurationLoaded: (YouTubePlayer, Float) -> Unit,
    onState: (YouTubePlayer, PlayerConstants.PlayerState) -> Unit,
    youtubeViewModel: YoutubeViewModel
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle;
    val ytPlayerHelper by youtubeViewModel.ytHelper.collectAsState()
    val mediaSession by youtubeViewModel.mediaSession.collectAsState()

    println(youtubeVideoId);
    AndroidView(
        modifier = Modifier.size(0.dp),
        factory = { context ->
            YouTubePlayerView(context = context).apply {
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
                            youtubeViewModel.onDurationLoaded(duration);
                            youtubeViewModel.updateVideoDuration(durationMs = duration.toLong() * 1000L);
                            youtubeViewModel.setMediaSessionActive(true);
                            // println("Playing");
                            youtubeViewModel.updatePlaybackState(
                                state = PlayerConstants.PlayerState.PLAYING,
                                position = 0,
                                playbackSpeed = 1.0f
                            );
                            ytPlayerHelper.play();
                        }
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        onSecond(youTubePlayer, second);
                        youtubeViewModel.onSecond(second);
                    }

                    override fun onStateChange(
                        youTubePlayer: YouTubePlayer,
                        state: PlayerConstants.PlayerState
                    ) {
                        onState(youTubePlayer, state);
                        if (state == PlayerConstants.PlayerState.UNSTARTED)
                            return;
                        if (state == PlayerConstants.PlayerState.ENDED) {
                            ytPlayerHelper.seekToTime = 0f;
                            youtubeViewModel.playerViewModel.changeSong(true, MainActivity.applicationContext);
                        }
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

fun getYoutubeSmallThumbnail(videoId: String) : String {
    return "https://img.youtube.com/vi/$videoId/mqdefault.jpg"
}

fun parseSyncedLyrics(syncedLyrics : String) : List<Line> {
    val lines : MutableList<Line> = mutableListOf();
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

suspend fun getLRCLIBLyrics(ytMusic : Ytmusic, track : String, artist : String, duration : Float) : Lyrics? {
    var lines : List<Line> = emptyList();
    var isSynced = false;
    val lyricsList = ytMusic.searchLrclibLyrics(track, artist).body<List<LRCLIBObject>>();
    if (lyricsList.isNotEmpty()) {
        var bestMatchDuration : Float = Float.MAX_VALUE;
        var lrclibObj : LRCLIBObject = lyricsList.reduce { result, item ->
            if (abs(item.duration - duration) < bestMatchDuration) {
                bestMatchDuration = item.duration - duration;
                return@reduce item;
            }
            result;
        };
        if (lrclibObj.syncedLyrics != null) {
            lrclibObj.syncedLyrics!!.let { lines = parseSyncedLyrics(it) };
            isSynced = true;
        }
        else if (lrclibObj.plainLyrics != null) {
            lrclibObj.plainLyrics!!.let { lines = parsePlainLyrics(it) };
            isSynced = false;
        }
        Log.d("Lyrics", "${lrclibObj.duration}");
    }
    if (lines.isEmpty())
        return null;
    else
        return Lyrics(lines = lines, isSynced = isSynced);
}