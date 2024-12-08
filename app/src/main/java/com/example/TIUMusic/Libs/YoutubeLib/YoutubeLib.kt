package com.example.TIUMusic.Libs.YoutubeLib

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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

@Composable
fun YoutubeView(
    youtubeVideoId: String,
    onSecond: (YouTubePlayer, Float) -> Unit,
    onDurationLoaded: (YouTubePlayer, Float) -> Unit,
    onState: (YouTubePlayer, PlayerConstants.PlayerState) -> Unit,
    youtubeViewModel: YoutubeViewModel
) {
    val ytPlayerHelper by youtubeViewModel.ytHelper.collectAsState()
    val mediaSession by youtubeViewModel.mediaSession.collectAsState()

    println(youtubeVideoId);
    AndroidView(
        modifier = Modifier.size(0.dp),
        factory = { context ->
            YouTubePlayerView(context = MainActivity.applicationContext).apply {
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
                            // ytPlayerHelper.play();
                        }
                    }

                    override fun onError(
                        youTubePlayer: YouTubePlayer,
                        error: PlayerConstants.PlayerError
                    ) {
                        super.onError(youTubePlayer, error)
                        println(error.name);
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
                        if (!ytPlayerHelper.isSeekBuffering)
                            ytPlayerHelper.seekToTime = ytPlayerHelper.ytVideoTracker.currentSecond;
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
    if (track.isEmpty() && artist.isEmpty())
        return null;
    var lines : List<Line> = emptyList();
    var isSynced = false;
    runCatching {
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
    }
    if (lines.isEmpty())
        return null;
    else
        return Lyrics(lines = lines, isSynced = isSynced);
}