package com.example.TIUMusic.Libs.YoutubeLib

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.example.TIUMusic.Libs.YoutubeLib.models.YouTubeClient
import com.example.TIUMusic.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TIUMusic.Libs.YoutubeLib.models.SearchResponse
import com.example.TIUMusic.Libs.YoutubeLib.models.VideoInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

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

    public fun seekTo(time : Float) {
        if (ytPlayer != null) {
            seekToTime = time;
            ytPlayer!!.seekTo(seekToTime);
            for (listener in mediaNotificationSeekListeners)
                listener.onSeek(time);
        }
    }

    public fun play() {
        ytPlayer?.play();
    }

    public fun pause() {
        ytPlayer?.pause();
    }
}

object YoutubeSettings {
    var NotificationEnabled = false;
}

class YoutubeHelper {
    var mediaSession : MediaSession? = null;
    var ytPlayerHelper : YoutubePlayerHelper = YoutubePlayerHelper();

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

    fun init(context: Context) {
        if (___ran)
            return;
        mediaSession = MediaSession(context, "MusicService");
        println("ran");
        assert(!___ran) {
            error("YoutubeViewModel should only run once");
        }
        ___ran = true;
        if (YoutubeSettings.NotificationEnabled) {
            var builder = Notification.Builder(context, Notification.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("it's not litter if you bin it")
                .setContentText("Niko B - dog eats dog food world")
                .setStyle(Notification.MediaStyle().setMediaSession(mediaSession!!.sessionToken))
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

            mediaSession!!.setCallback(object : MediaSession.Callback() {
                override fun onPlay() {
                    ytPlayerHelper.play();
                }

                override fun onPause() {
                    ytPlayerHelper.pause();
                }

                override fun onSeekTo(pos: Long) {
                    ytPlayerHelper.seekTo(pos / 1000f);
                }
            })
        }
    }

    fun updateYoutubePlayer(ytPlayer : YouTubePlayer) {
        ytPlayerHelper.ytPlayer = ytPlayer;
        ytPlayerHelper.ytPlayer?.addListener(ytPlayerHelper.ytVideoTracker);
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
        artBitmap : Bitmap? = null
    ) {
        if (!YoutubeSettings.NotificationEnabled)
            return;
        val metadataBuilder = MediaMetadata.Builder().apply {
            // To provide most control over how an item is displayed set the
            // display fields in the metadata
            putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, displayTitle)
            putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, displaySubtitle)
            // putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, myData.artUri)
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
        mediaSession?.setMetadata(metadataBuilder.build());
    }

    fun updatePlaybackState(state : PlayerConstants.PlayerState, position : Long, playbackSpeed : Float) {
        if (!YoutubeSettings.NotificationEnabled)
            return;
        mediaSession?.setPlaybackState(
            PlaybackState.Builder()
                .setActions(availableActions)
                .setState(getState(state), position, playbackSpeed)
                .build()
        )
    }

    fun updatePlaybackState(state : Int, position : Long, playbackSpeed : Float) {
        if (!YoutubeSettings.NotificationEnabled)
            return;
        mediaSession?.setPlaybackState(
            PlaybackState.Builder()
                .setActions(availableActions)
                .setState(state, position, playbackSpeed)
                .build()
        )
    }

    fun setMediaSessionActive(active : Boolean) {
        if (!YoutubeSettings.NotificationEnabled)
            return;
        if (mediaSession != null)
            mediaSession!!.isActive = true;
    }

    fun addMediaNotificationSeekListener(listener: MediaNotificationSeek) {
        ytPlayerHelper.addMediaNotificationSeekListener(listener);
    }

}

@HiltViewModel
class YtmusicViewModel @Inject constructor(
    private val ytmusic: Ytmusic // Inject Ytmusic class (nếu dùng Hilt hoặc tạo instance thủ công)
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    fun performSearch(query: String) : List<VideoInfo>? {
        var videoInfos: List<VideoInfo>? = null
        Log.d("viewModelTest", "RUN")
        viewModelScope.launch {
            _loading.value = true
            try {
                withContext(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
                    Log.e("viewModelTest", "Lỗi trong coroutine: ${throwable.message}")
                }) {
                    Log.d("viewModelTest", "RUNinside-1")
                    val client = YouTubeClient.WEB_REMIX
                    val response = ytmusic.search(client = client, "ade").bodyAsText()
                    Log.d("viewModelTest", "RUNinside-1.5")
                    // Phần còn lại của mã
                }
            } catch (e: Exception) {
                // Xử lý ngoại lệ ở đây
                videoInfos = null
                Log.d("viewModelTest", "Error occurred: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
        return videoInfos
    }
    override fun onCleared() {
        super.onCleared()
        Log.d("viewModelTest", "ViewModel is cleared")
    }
    // Hàm trích xuất thông tin video ID
    fun extractVideoInfo(response: SearchResponse): List<VideoInfo> {
        // Lấy tabs đầu tiên
        val firstTab = response.contents.tabbedSearchResultsRenderer.tabs.firstOrNull()
            ?: throw Exception("No tabs found")

        // Duyệt qua từng tab và lấy thông tin video
        val videoInfos = mutableListOf<VideoInfo>()

        // Lấy musicCardShelfRenderer
        val musicShelf = firstTab.tabRenderer.content.sectionListRenderer.contents.firstOrNull()?.musicCardShelfRenderer
            ?: throw Exception("No music card shelf found")


        //Lấy các run
        val runs = musicShelf.title.runs
        Log.d("viewModelTest","Count RUNS: ${runs.size}")
        for (run in runs){
            // Tìm tiêu đề
            val title = run.text
            // Tìm video ID từ navigation endpoint
            val videoId = run.navigationEndpoint?.watchEndpoint?.videoId
                ?: run.navigationEndpoint?.browseEndpoint?.browseId
            videoInfos.add(
                VideoInfo(
                    videoId = videoId,
                    title = title
                )
            )
        }

        return videoInfos
    }
}

@Module
@InstallIn(SingletonComponent::class)
object YtmusicModule {
    @Provides
    fun provideYtmusic(): Ytmusic {
        return Ytmusic()
    }
}





