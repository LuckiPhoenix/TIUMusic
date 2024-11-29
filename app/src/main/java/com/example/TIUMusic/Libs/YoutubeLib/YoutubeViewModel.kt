package com.example.TIUMusic.Libs.YoutubeLib

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import com.example.TIUMusic.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface MediaNotificationSeek {
    fun onSeek(seekTime : Float);
}

class YoutubePlayerHelper {
    var ytPlayer : YouTubePlayer? = null;
    var ytVideoTracker : YouTubePlayerTracker = YouTubePlayerTracker();
    var seekToTime : Float = 0.0f
    var mediaNotificationSeekListeners = ArrayList<MediaNotificationSeek>();

    val duration : Float get() = ytVideoTracker.videoDuration;
    val currentSecond : Float get() = ytVideoTracker.currentSecond;

    public fun addMediaNotificationSeekListener(listener : MediaNotificationSeek) {
        mediaNotificationSeekListeners.add(listener);
    }

    public fun seekTo(second : Float) {
        if (ytPlayer != null) {
            seekToTime = second;
            ytPlayer!!.seekTo(seekToTime);
            for (listener in mediaNotificationSeekListeners)
                listener.onSeek(second);
        }
    }

    public fun play() {
        seekToTime = currentSecond;
        ytPlayer?.play();
    }

    public fun pause() {
        seekToTime = currentSecond;
        ytPlayer?.pause();
    }
}

object YoutubeSettings {
    var NotificationEnabled = false;
}

class YoutubeViewModel(context : Context) : ViewModel() {
    private val _mediaSession = MutableStateFlow(MediaSession(context, "MusicService"));
    val mediaSession : StateFlow<MediaSession> = _mediaSession.asStateFlow();

    private val _ytHelper = MutableStateFlow(YoutubePlayerHelper());
    val ytHelper : StateFlow<YoutubePlayerHelper> = _ytHelper.asStateFlow();

    private var _ytPlayerView : MutableStateFlow<YouTubePlayerView?> = MutableStateFlow(null);
    val ytPlayerView : StateFlow<YouTubePlayerView?> = _ytPlayerView.asStateFlow();


    val NotificationID = 0;

    companion object {
        var ___ran : Boolean = false;
        val availableActions : Long =
            (PlaybackState.ACTION_SEEK_TO
                    or PlaybackState.ACTION_PAUSE
                    or PlaybackState.ACTION_STOP
                    or PlaybackState.ACTION_PLAY
                    or PlaybackState.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackState.ACTION_SKIP_TO_NEXT);

        private fun getState(state : PlayerConstants.PlayerState) : Int {
            return when (state) {
                PlayerConstants.PlayerState.PLAYING -> PlaybackState.STATE_PLAYING;
                PlayerConstants.PlayerState.ENDED -> PlaybackState.STATE_STOPPED;
                PlayerConstants.PlayerState.PAUSED -> PlaybackState.STATE_PAUSED;
                PlayerConstants.PlayerState.UNKNOWN -> PlaybackState.STATE_NONE;
                PlayerConstants.PlayerState.BUFFERING -> PlaybackState.STATE_BUFFERING;
                PlayerConstants.PlayerState.UNSTARTED -> PlaybackState.STATE_NONE;
                PlayerConstants.PlayerState.VIDEO_CUED -> PlaybackState.STATE_NONE;
            }
        }
    }

    init {
        if (___ran) {
            error("Shit went wrong");
        }

        ___ran = true;
        println("1");
        var builder = Notification.Builder(context, Notification.CATEGORY_MESSAGE)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("it's not litter if you bin it")
            .setContentText("Niko B - dog eats dog food world")
            .setStyle(Notification.MediaStyle().setMediaSession(mediaSession.value.sessionToken))
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("nope");
                return@with
            }
            // notificationId is a unique int for each notification that you must define.
            notify(NotificationID, builder.build())
        }

        _mediaSession.value.setCallback(object : MediaSession.Callback() {
            override fun onPlay() {
                _ytHelper.update { current ->
                    current.play();
                    current;
                }
            }

            override fun onPause() {
                _ytHelper.update { current ->
                    current.pause();
                    current;
                }
            }

            override fun onSeekTo(pos: Long) {
                _ytHelper.update { current ->
                    current.seekTo(pos / 1000f);
                    current;
                }
            }
        })
    }

    fun updateYoutubePlayerView(youTubePlayerView: YouTubePlayerView) {
        _ytPlayerView.update { youTubePlayerView }
    }

    fun updateYoutubePlayer(ytPlayer : YouTubePlayer) {
        _ytHelper.update { it ->
            it.ytPlayer = ytPlayer;
            it.ytPlayer?.addListener(it.ytVideoTracker);
            it;
        }
    }

    fun updateMediaMetadata(metadata: YoutubeMetadata, durationMs: Long) {
        updateMediaMetadata(
            metadata.displayTitle,
            metadata.displaySubtitle,
            metadata.title,
            metadata.artist,
            durationMs,
            metadata.artBitmap
        )
    }

    fun updateMediaMetadata(
        displayTitle : String,
        displaySubtitle : String,
        title : String,
        artist : String,
        durationMs : Long,
        artBitmap : Bitmap?
    ) {
        val metadataBuilder = MediaMetadata.Builder().apply {
            // To provide most control over how an item is displayed set the
            // display fields in the metadata
            putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, displayTitle)
            putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, displaySubtitle)
            // And at minimum the title and artist for legacy support
            putString(MediaMetadata.METADATA_KEY_TITLE, title)
            putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
            putLong(MediaMetadata.METADATA_KEY_DURATION, durationMs)
            // A small bitmap for the artwork is also recommended
            if (artBitmap != null)
                putBitmap(MediaMetadata.METADATA_KEY_ART, artBitmap)
            // Add any other fields you have for your data as well
        }
        // println("wtf");
        _mediaSession.update { it ->
            it.setMetadata(metadataBuilder.build());
            it;
        }
    }

    fun updatePlaybackState(state : PlayerConstants.PlayerState, position : Long, playbackSpeed : Float) {
        _mediaSession.update { it ->
            it.setPlaybackState(
                PlaybackState.Builder()
                    .setActions(availableActions)
                    .setState(getState(state), position, playbackSpeed)
                    .build()
            )
            it;
        }
    }

    fun updatePlaybackState(state : Int, position : Long, playbackSpeed : Float) {
        _mediaSession.update { it ->
            it.setPlaybackState(
                PlaybackState.Builder()
                    .setActions(availableActions)
                    .setState(state, position, playbackSpeed)
                    .build()
            )
            it;
        }
    }

    fun setMediaSessionActive(active : Boolean) {
        _mediaSession.update { it ->
            it.isActive = true;
            it;
        }
    }

    fun addMediaNotificationSeekListener(listener: MediaNotificationSeek) {
        _ytHelper.update { it ->
            it.addMediaNotificationSeekListener(listener);
            it;
        }
    }

}