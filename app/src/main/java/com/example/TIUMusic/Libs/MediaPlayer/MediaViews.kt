package com.example.TIUMusic.Libs.MediaPlayer

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * Composable function to display the audio player view.
 * This composable sets up and displays the ExoPlayer view for audio playback.
 * @param viewModel The view model for managing media playback.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun AudioPlayerView(viewModel: MediaViewModel) {
    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring ExoPlayer
    val mExoPlayer = remember(viewModel.player) {
        ExoPlayer.Builder(mContext).build().apply {
            viewModel.preparePlayer(context = mContext)
        }
    }

    DisposableEffect(Unit) {
        // Disposes the player when the composable is removed from the composition
        onDispose { viewModel.onDestroy() }
    }

    // Implementing ExoPlayer
    AndroidView(modifier = Modifier.size(0.dp),
        factory = { context ->
            PlayerView(context).apply {
                this.player = mExoPlayer
                hideController()
                useController = false
                controllerHideOnTouch = false
            }
        })
}