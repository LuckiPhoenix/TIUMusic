package com.example.TIUMusic.SongData

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel : ViewModel() {
    private var _musicItem = MutableStateFlow(MusicItem("", "", "", "", 0));
    val musicItem = _musicItem.asStateFlow();

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

    init {
        println("lmao");
    }

    fun setMusicItem(item : MusicItem) {
        _musicItem.value = item;
    }

    fun setCurrentTime(currTime : Float) {
        _currentTime.floatValue = currTime;
    }

    fun setDuration(duration : Float) {
        _duration.floatValue = duration;
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
