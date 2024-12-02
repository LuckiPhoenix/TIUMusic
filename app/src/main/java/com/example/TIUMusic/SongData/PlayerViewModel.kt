package com.example.TIUMusic.SongData

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeMetadata
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeViewModel
import com.example.TIUMusic.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayerViewModel : ViewModel() {
    private var _musicItem = MutableStateFlow(MusicItem("", "", "", "", 0));
    val musicItem = _musicItem.asStateFlow();

    private var _playlist : MutableStateFlow<List<MusicItem>?> = MutableStateFlow(null);
    val playlist = _playlist.asStateFlow();

    private var _currentPlaylistIndex : MutableState<Int?> = mutableStateOf(null);
    val currentPlaylistIndex : State<Int?> = _currentPlaylistIndex;

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _expanded = mutableStateOf(false)
    val expanded: State<Boolean> = _expanded

    private val _offsetY = mutableStateOf(0f)
    val offsetY: State<Float> = _offsetY

    private val _duration = mutableFloatStateOf(0.0f);
    val duration: State<Float> = _duration;

    private val _currentTime = mutableFloatStateOf(0.0f);
    val currentTime : State<Float> = _currentTime;

    var ytViewModel = YoutubeViewModel(this);

    fun playSong(item : MusicItem, context : android.content.Context) {
        _musicItem.value = item;
        ytViewModel.loadAndPlayVideo(
            videoId = item.videoId,
            metadata = YoutubeMetadata(
                title = item.title,
                artist = item.artist,
                artBitmapURL = item.imageUrl,
                displayTitle = item.title,
            ),
            durationMs = 0,
            context = context
        )
    }

    fun setPlaylist(items : List<MusicItem>?) {
        _playlist.value = items;
    }

    fun changeSong(nextSong: Boolean, context: android.content.Context) : Boolean {
        if (playlist.value != null &&
            currentPlaylistIndex.value != null &&
            currentPlaylistIndex.value!! + (if (nextSong) 1 else -1)
                in (0 .. playlist.value!!.size - 1)) {
            setCurrentTime(0f);
            _currentPlaylistIndex.value = _currentPlaylistIndex.value!! + (if (nextSong) 1 else -1);
            playSong(playlist.value!![currentPlaylistIndex.value!!], context);
            return true;
        }
        return false;
    }

    fun shufflePlaylist() {
        _playlist.value?.shuffled();
    }

    fun playSongInPlaylistAtIndex(index : Int?, context: android.content.Context) {
        _currentPlaylistIndex.value = index;
        if (index != null && playlist.value != null) {
            playSong(_playlist.value!![currentPlaylistIndex.value!!], context);
        }
    }

    fun resetPlaylist() {
        _playlist.value = null;
        _currentPlaylistIndex.value = null;
    }

    fun setCurrentTime(currTime : Float) {
        _currentTime.floatValue = currTime;
    }

    fun seekTo(time : Float) {
        ytViewModel.ytHelper.value.seekTo(time);
    }

    fun setDuration(duration : Float) {
        _duration.floatValue = duration;
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
//        if (playing)
//            ytViewModel.ytHelper.value.play();
//        else
//            ytViewModel.ytHelper.value.pause();
    }

    fun setExpanded(expand: Boolean) {
        _expanded.value = expand
    }

    fun updateOffset(offset: Float) {
        _offsetY.value = offset
    }
}
