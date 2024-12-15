package com.example.TIUMusic.Libs.MediaPlayer

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.session.MediaSession
import com.example.TIUMusic.MediaChannelID
import com.example.TIUMusic.MediaNotificationID
import com.example.TIUMusic.R


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MediaNotificationManager(
    private val context: Context,
    private val mediaSession: MediaSession
) {
    private var notificationBuilder : Notification.Builder;

    init {
        notificationBuilder = Notification.Builder(context, MediaChannelID)
            .setSmallIcon(R.drawable.tiumusicmark)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setStyle(Notification.MediaStyle().setMediaSession(mediaSession.platformToken))
            .setAutoCancel(false)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= 30)
            notificationBuilder.setFlag(Notification.FLAG_NO_CLEAR, true);

//        with(NotificationManagerCompat.from(context)) {
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                println("nope");
//                return@with
//            }
//            // notificationId is a unique int for each notification that you must define.
//            notify(MediaNotificationID, notificationBuilder.build());
//        }

    }

    fun hideNotification() {
    }

    fun showNotificationForPlayer(title : String, artist : String, albumArt : Int?) {
        println("Hello");
        notificationBuilder = Notification.Builder(context, MediaChannelID)
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
            notify(MediaNotificationID, notificationBuilder.build());
        }
    }
}