package com.example.TIUMusic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.TIUMusic.Libs.Visualizer.VisualizerSettings
import com.example.TIUMusic.Libs.Visualizer.VisualizerViewModel
import com.example.TIUMusic.Libs.YoutubeLib.YouTube.ytMusic
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeSettings
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeViewModel
import com.example.TIUMusic.Libs.YoutubeLib.createNotificationChannel
import com.example.TIUMusic.Libs.YoutubeLib.models.YouTubeClient.Companion.WEB_REMIX
import com.example.TIUMusic.Libs.YoutubeLib.models.response.BrowseResponse
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.ui.theme.TIUMusicTheme
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val playerViewModel = PlayerViewModel() // Could be a bad idea
        val visualizerViewModel = VisualizerViewModel()
        val youtubeViewModel = YoutubeViewModel()
        requestPermissions(
            activity = this,
            onAccepted = { name ->
                when (name) {
                    Manifest.permission.RECORD_AUDIO -> {
                        VisualizerSettings.VisualizerEnabled = true;
                        visualizerViewModel.init();
                    }
                    Manifest.permission.POST_NOTIFICATIONS -> {
                        YoutubeSettings.NotificationEnabled = true;
                        youtubeViewModel.init(this);
                    }
                }
            },
            onDenied = { name ->
                when (name) {
                    Manifest.permission.RECORD_AUDIO -> {
                        VisualizerSettings.VisualizerEnabled = false;
                    }
                    Manifest.permission.POST_NOTIFICATIONS -> {
                        YoutubeSettings.NotificationEnabled = false;
                    }
                }
            }
        )
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

fun hasPermissions(activity: ComponentActivity, permissions : Array<String>) : Boolean {
    for (permission in permissions) {
        if (checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            return false;
        }
    }
    return true;
}

fun requestPermissions(
    activity : ComponentActivity,
    onAccepted : (String) -> Unit,
    onDenied : (String) -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
        return;

    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.POST_NOTIFICATIONS
    );

    val requestPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permits ->
            for (permit in permits) {
                if (permit.value) {
                    onAccepted(permit.key);
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    onDenied(permit.key);
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        }

    if (hasPermissions(activity, permissions)){
        for (permission in permissions) {
            onAccepted(permission);
        }
        Log.d("Permissions", "Yay!");
    }
    else {
        requestPermissionLauncher.launch(permissions);
    }
}
