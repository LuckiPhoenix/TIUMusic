package com.example.TIUMusic.SongData

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.viewModelScope
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
    private var _musicItem = MutableStateFlow(MusicItem("", "", "", "", 0));
    val musicItem = _musicItem.asStateFlow();

    private var _playlist : MutableStateFlow<MutableList<MusicItem>?> = MutableStateFlow(null);
    val playlist = _playlist.asStateFlow();
    
    private var shuffledPlaylist : List<MusicItem>? = listOf();
    private var isShuffled : Boolean = false;

    private var _currentPlaylistIndex : MutableState<Int?> = mutableStateOf(null);
    val currentPlaylistIndex : State<Int?> = _currentPlaylistIndex;

    var lyrics : Lyrics? = null;
    var currentSyncedIndex : Int = 0
    private var _syncedLine = MutableStateFlow<Line>(Line(0f, ""))
    val syncedLine = _syncedLine.asStateFlow();

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

    var ytViewModel = YoutubeViewModel(this);
    val syncedLyricsBuffer : Float = 0.0f;

    init {
        ytViewModel.onSecond = { second ->
            if (lyrics != null && lyrics!!.isSynced) {
                if (currentSyncedIndex < lyrics!!.lines.size &&
                    second + syncedLyricsBuffer >= lyrics!!.lines[currentSyncedIndex].startSeconds
                ) {
                    _syncedLine.value = lyrics!!.lines[currentSyncedIndex];
                    currentSyncedIndex++;
                }
            }
            else
                _syncedLine.value = Line(0f, "");
        }
        ytViewModel.onDurationLoaded = { second ->
            getLyrics(musicItem.value.title, musicItem.value.artist, second);
        }
        ytViewModel.addSeekListener(object : SeekListener {
            override fun onSeek(seekTime: Float) {
                currentSyncedIndex = 0;
                _syncedLine.value = Line(0f, "");
                if (lyrics != null && lyrics!!.isSynced){
                    currentSyncedIndex = (lyrics!!.lines.indexOfFirst { it ->
                        ytViewModel.ytHelper.value.seekToTime < it.startSeconds;
                    } - 1).coerceIn(0, lyrics!!.lines.size - 1);
                }
                else
                    _syncedLine.value = Line(0f, "");
            }
        });
    }

    fun playSong(item : MusicItem, context : android.content.Context, expandPlayer: Boolean = false) {
        _musicItem.value = item;
        if (expandPlayer)
            setShouldExpand(1);
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
        _playlist.value = items as MutableList<MusicItem>?;
    }

    fun setRadio(song: MusicItem) {
        viewModelScope.launch {
            resetPlaylist();
            _currentPlaylistIndex.value = 0;
            setPlaylist(YtmusicViewModel.getRadio(song.videoId, song)?.tracks);
        }
    }

    fun changeSong(nextSong: Boolean, context: android.content.Context) : Boolean {
        if (playlist.value != null &&
            currentPlaylistIndex.value != null &&
            currentPlaylistIndex.value!! + (if (nextSong) 1 else -1)
                in (0 .. playlist.value!!.size - 1)) {
            setCurrentTime(0f);
            _currentPlaylistIndex.value = _currentPlaylistIndex.value!! + (if (nextSong) 1 else -1);
            playSongInPlaylistAtIndex(_currentPlaylistIndex.value, context);
            return true;
        }
        return false;
    }

    fun shufflePlaylist() {
        setIsShuffled(true);
        shuffledPlaylist = _playlist.value?.shuffled();
    }

    fun playSongInPlaylistAtIndex(index : Int?, context: android.content.Context, expandPlayer: Boolean = false) {
        _currentPlaylistIndex.value = index;
        if (index != null && playlist.value != null) {
            if (!isShuffled && playlist.value != null)
                playSong(_playlist.value!![currentPlaylistIndex.value!!], context, expandPlayer);
            else if (isShuffled && shuffledPlaylist != null)
                playSong(shuffledPlaylist!![currentPlaylistIndex.value!!], context, expandPlayer);
        }
    }

    fun playlistInsertNext(musicItems : List<MusicItem>) {
        if (currentPlaylistIndex.value != null) {
            _playlist.value?.addAll(currentPlaylistIndex.value!! + 1, musicItems);
            if ((_playlist.value?.size ?: 0) > 100) {
                _playlist.value = _playlist.value?.subList(0, 100);
            }
        }
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
        _currentPlaylistIndex.value = null;
    }

    fun getLyrics(track: String, artist : String, duration: Float) {
        currentSyncedIndex = 0;
        lyrics = null;
        _syncedLine.value = Line(0f, "");
        viewModelScope.launch {
            lyrics = getLRCLIBLyrics(YouTube.ytMusic, track, artist, duration);
        }
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

    fun setIsShuffled(value : Boolean) {
        isShuffled = value;
    }

    fun setShouldExpand(should : Int) {
        _shouldExpand.value = should;
        _shouldExpand.value.coerceIn(-1, 1);
    }

    fun setLoop(value : Boolean) {
        _loop.value = value;
        Log.d("Loop", _loop.value.toString());
        ytViewModel.ytHelper.value.ytPlayer?.setLoop(value);
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
