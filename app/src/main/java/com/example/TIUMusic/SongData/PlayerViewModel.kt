package com.example.TIUMusic.SongData

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.platform.LocalContext
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
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.MainActivity
import com.example.TIUMusic.MusicDB.MusicViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    val musicViewModel = MusicViewModel(MainActivity.applicationContext);
    var authViewModel : UserViewModel? = null;

    fun playSong(item : MusicItem, context : Context, expandPlayer: Boolean = false) {
        _musicItem.value = item;
        if (expandPlayer)
            setShouldExpand(1);
        _mediaViewModel.value?.setShuffled(false);
        setPlaylist(listOf(item));
        playSongInPlaylistAtIndex(0, context, expandPlayer, true);
        viewModelScope.launch {
            musicViewModel.getRandomSongs(10, context).collectLatest {
                val radio = it.toMutableList();
                radio.add(0, item);
                setPlaylist(radio);
                playSongInPlaylistAtIndex(0, context, expandPlayer, false);
            };
        }
    }

    fun setMediaViewModel(viewModel : MediaViewModel, authViewModel: UserViewModel) {
        _mediaViewModel.value = viewModel;
        this.authViewModel = authViewModel;
        _mediaViewModel.value!!.mediaTransitionListener = { index ->
            if (!playlist.value.isNullOrEmpty()) {
                _musicItem.value = playlist.value!![index];
                if (musicItem.value.songId != null)
                    authViewModel.listenTo(musicItem.value.songId!!);
            }
        }
    }

    fun setPlaylist(items : List<MusicItem>?) {
        _playlist.value = items as MutableList<MusicItem>?;
    }

    fun changeSong(nextSong: Boolean, context: android.content.Context) : Boolean {
        return false;
    }

    fun playSongInPlaylistAtIndex(index : Int?, context: Context, expandPlayer: Boolean = false, reset : Boolean = true) {
        if (index != null && playlist.value != null) {
            if (playlist.value != null)
                _mediaViewModel.value?.setPlaylist(context, playlist.value!!, index, reset);
            if (expandPlayer)
                setShouldExpand(1);
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
