package com.example.TIUMusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import com.example.TIUMusic.Libs.Visualizer.VisualizerViewModel
import com.example.TIUMusic.Libs.Visualizer.ensureVisualizerPermissionAllowed
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeViewModel
import com.example.TIUMusic.Libs.YoutubeLib.createNotificationChannel
import com.example.TIUMusic.Libs.YoutubeLib.ensurePlayerNotificationPermissionAllowed
import com.example.TIUMusic.Screens.NowPlayingSheet
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.ui.theme.TIUMusicTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ensureVisualizerPermissionAllowed(this);
        ensurePlayerNotificationPermissionAllowed(this);
        createNotificationChannel(this);
        val playerViewModel = PlayerViewModel() // Could be a bad idea
        val visualizerViewModel = VisualizerViewModel(captureSize = 256)
        val youtubeViewModel = YoutubeViewModel(this)
        setContent {
            TIUMusicTheme {
                NavHost(
                    playerViewModel = playerViewModel,
                    visualizerViewModel = visualizerViewModel,
                    youtubeViewModel = youtubeViewModel
                )
            }
        }
    }
}
