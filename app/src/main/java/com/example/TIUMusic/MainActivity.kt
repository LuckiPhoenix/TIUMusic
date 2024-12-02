package com.example.TIUMusic

import android.Manifest
import android.app.NotificationManager
import android.content.Context
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
import com.example.TIUMusic.Libs.YoutubeLib.MediaNotificationID
import com.example.TIUMusic.Libs.YoutubeLib.YoutubeSettings
import com.example.TIUMusic.Libs.YoutubeLib.createTestBitmap
import com.example.TIUMusic.SongData.PlayerViewModel
import com.example.TIUMusic.ui.theme.TIUMusicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        var isPaused = false;

        private lateinit var appContext : Context;

        public val applicationContext: Context
            get() = appContext;
    }

    override fun onPause() {
        super.onPause()
        isPaused = true;
    }

    override fun onResume() {
        super.onResume()
        isPaused = false;
    }

    override fun onDestroy() {
        super.onDestroy()
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(MediaNotificationID);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appContext = this;
        val playerViewModel = PlayerViewModel() // Could be a bad idea
        val visualizerViewModel = VisualizerViewModel()
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
                        playerViewModel.ytViewModel.init(this);
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
        createTestBitmap(this);
        setContent {
            TIUMusicTheme {

                NavHost(
                    playerViewModel = playerViewModel,
                    visualizerViewModel = visualizerViewModel
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
