package com.example.TIUMusic.Libs.MediaPlayer

import android.Manifest
import android.R.attr.bitmap
import android.app.Notification
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.TIUMusic.Libs.YoutubeLib.MediaNotificationID
import com.example.TIUMusic.MainActivity
import com.example.TIUMusic.R
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A wrapper class for ExoPlayer's PlayerNotificationManager.
 * It sets up the notification shown to the user during audio playback and provides track metadata,
 * such as track title and icon image.
 * @param context The context used to create the notification.
 * @param sessionToken The session token used to build MediaController.
 * @param player The ExoPlayer instance.
 * @param notificationListener The listener for notification events.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MediaNotificationManager(
    private val context: Context,
    private val mediaSession: MediaSession,
    private val player: Player,
    notificationListener: PlayerNotificationManager.NotificationListener
) {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var notificationBuilder : Notification.Builder;

    init {
        notificationBuilder = Notification.Builder(context, NOW_PLAYING_CHANNEL_ID)
            .setSmallIcon(R.drawable.tiumusicmark)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setStyle(Notification.MediaStyle().setMediaSession(mediaSession.platformToken))
            .setAutoCancel(false)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= 30)
            notificationBuilder.setFlag(Notification.FLAG_NO_CLEAR, true);

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("nope");
                return@with
            }
            // notificationId is a unique int for each notification that you must define.
            notify(NOW_PLAYING_NOTIFICATION_ID, notificationBuilder.build());
        }

    }


    /**
     * Hides the notification.
     */
    fun hideNotification() {
    }

    /**
     * Shows the notification for the given player.
     * @param player The player instance for which the notification is shown.
     */
    fun showNotificationForPlayer(title : String, artist : String, albumArt : Int?) {
        notificationBuilder = Notification.Builder(context, NOW_PLAYING_CHANNEL_ID)
            .setSmallIcon(R.drawable.tiumusicmark)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setContentTitle(title)
            .setContentText(artist)
            .setStyle(Notification.MediaStyle().setMediaSession(mediaSession.platformToken))
            .setAutoCancel(false)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= 30)
            notificationBuilder.setFlag(Notification.FLAG_NO_CLEAR, true);
        if (albumArt != null)
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.resources, albumArt));

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("nope");
                return@with
            }
            // notificationId is a unique int for each notification that you must define.
            notify(NOW_PLAYING_NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}

/**
 * The size of the large icon for the notification in pixels.
 */
const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

/**
 * The channel ID for the notification.
 */
const val NOW_PLAYING_CHANNEL_ID = "media.NOW_PLAYING"

/**
 * The notification ID.
 */
const val NOW_PLAYING_NOTIFICATION_ID = 0xb339 // Arbitrary number used to identify our notification

/**
 * Default options for Glide.
 */
private val glideOptions = RequestOptions()
    .fallback(R.drawable.tiumarksvg)
    .diskCacheStrategy(DiskCacheStrategy.DATA)