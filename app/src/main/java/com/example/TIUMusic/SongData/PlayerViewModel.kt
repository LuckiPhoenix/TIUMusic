package com.example.TIUMusic.SongData

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

class PlayerViewModel : ViewModel() {
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _expanded = mutableStateOf(false)
    val expanded: State<Boolean> = _expanded

    private val _offsetY = mutableStateOf(0f)
    val offsetY: State<Float> = _offsetY

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
