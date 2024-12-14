package com.example.TIUMusic.SongData

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.TIUMusic.Libs.MediaPlayer.MediaViewModel
import com.example.TIUMusic.Libs.YoutubeLib.SeekListener
import com.example.TIUMusic.Libs.YoutubeLib.YouTube
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeMetadata
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YtmusicViewModel
import com.example.TIUMusic.Libs.YoutubeLib.getLRCLIBLyrics
import com.example.TIUMusic.Libs.YoutubeLib.models.Line
import com.example.TIUMusic.Libs.YoutubeLib.models.Lyrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {
    private var _musicItem = MutableStateFlow(MusicItem("", "", "", "", MusicItemType.Song));
    val musicItem = _musicItem.asStateFlow();

    private var _playlist : MutableStateFlow<MutableList<MusicItem>?> = MutableStateFlow(null);
    val playlist = _playlist.asStateFlow();

    private var isShuffled : Boolean = false;

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _expanded = mutableStateOf(false)
    val expanded: State<Boolean> = _expanded

    private val _loop = mutableStateOf(false)
    val loop: State<Boolean> = _loop

    private val _offsetY = mutableStateOf(0f)
    val offsetY: State<Float> = _offsetY

    private val _duration = mutableFloatStateOf(0.0f);
    val duration: State<Float> = _duration;

    private val _currentTime = mutableFloatStateOf(0.0f);
    val currentTime : State<Float> = _currentTime;

    private val _shouldExpand = MutableStateFlow<Int>(0);
    val shouldExpand = _shouldExpand.asStateFlow();

    private var _mediaViewModel = MutableStateFlow<MediaViewModel?>(null);
    val mediaViewModel = _mediaViewModel.asStateFlow();

    fun playSong(item : MusicItem, context : Context, expandPlayer: Boolean = false) {
        _musicItem.value = item;
        if (expandPlayer)
            setShouldExpand(1);
        _mediaViewModel.value?.setMusicItem(item, context);
    }

    fun setMediaViewModel(viewModel : MediaViewModel) {
        _mediaViewModel.value = viewModel;
        _mediaViewModel.value!!.mediaTransitionListener = { index ->
            if (!playlist.value.isNullOrEmpty())
                _musicItem.value = playlist.value!![index];
        }
    }

    fun setPlaylist(items : List<MusicItem>?) {
        _playlist.value = items as MutableList<MusicItem>?;
    }

    fun setRadio(song: MusicItem) {
//        viewModelScope.launch {
//            resetPlaylist();
//            _currentPlaylistIndex.value = 0;
//            setPlaylist(YtmusicViewModel.getRadio(song.videoId, song)?.tracks);
//        }
    }

    fun changeSong(nextSong: Boolean, context: android.content.Context) : Boolean {
//        if (playlist.value != null &&
//            currentPlaylistIndex.value != null &&
//            currentPlaylistIndex.value!! + (if (nextSong) 1 else -1)
//                in (0 .. playlist.value!!.size - 1)) {
//            setCurrentTime(0f);
//            _currentPlaylistIndex.value = _currentPlaylistIndex.value!! + (if (nextSong) 1 else -1);
//            playSongInPlaylistAtIndex(_currentPlaylistIndex.value, context);
//            return true;
//        }
        return false;
    }

    fun playSongInPlaylistAtIndex(index : Int?, context: Context, expandPlayer: Boolean = false) {
        if (index != null && playlist.value != null) {
            if (playlist.value != null)
                _mediaViewModel.value?.setPlaylist(context, playlist.value!!, index);
            if (expandPlayer)
                setShouldExpand(1);
        }
    }

    fun playlistInsertNext(musicItems : List<MusicItem>) {
//        if (currentPlaylistIndex.value != null) {
//            _playlist.value?.addAll(currentPlaylistIndex.value!! + 1, musicItems);
//            if ((_playlist.value?.size ?: 0) > 100) {
//                _playlist.value = _playlist.value?.subList(0, 100);
//            }
//        }
    }

    fun playlistSortBy(option : String) {
        when (option) {
            "Title" -> _playlist.value?.sortBy { it.title };
            "Artist" -> _playlist.value?.sortBy { it.artist };
            "Playlist Order" -> { TODO() }
            else -> {}
        }
    }

    fun resetPlaylist() {
        _playlist.value = null;
    }

    fun setCurrentTime(currTime : Float) {
        _currentTime.floatValue = currTime;
    }

    fun setDuration(duration : Float) {
        _duration.floatValue = duration;
    }

    fun setIsShuffled(value : Boolean) {
        isShuffled = value;
        _mediaViewModel.value?.setShuffled(isShuffled);
    }

    fun setShouldExpand(should : Int) {
        _shouldExpand.value = should;
        _shouldExpand.value.coerceIn(-1, 1);
    }

    fun setLoop(value : Boolean) {
        _loop.value = value;
        _mediaViewModel.value?.setLoop(loop.value);
        Log.d("Loop", _loop.value.toString());
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun setExpanded(expand: Boolean) {
        _expanded.value = expand
    }

    fun updateOffset(offset: Float) {
        _offsetY.value = offset
    }
}
