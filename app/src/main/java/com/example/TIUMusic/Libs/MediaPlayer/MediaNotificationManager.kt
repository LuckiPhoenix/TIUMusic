package com.example.TIUMusic.Libs.MediaPlayer

import android.R.attr.bitmap
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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
    sessionToken: SessionToken,
    private val player: Player,
    notificationListener: PlayerNotificationManager.NotificationListener
) {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val notificationManager: PlayerNotificationManager

    init {

        val mediaController = MediaController.Builder(context, sessionToken).buildAsync()

        notificationManager = PlayerNotificationManager.Builder(
            context,
            NOW_PLAYING_NOTIFICATION_ID,
            NOW_PLAYING_CHANNEL_ID
        )
            .setChannelNameResourceId(R.string.media_notification_channel)
            .setChannelDescriptionResourceId(R.string.media_notification_channel_description)
            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            .setNotificationListener(notificationListener)
            .setSmallIconResourceId(R.drawable.tiumarksvg)
            .build()
            .apply {
                setPlayer(player)
                setUseRewindAction(true)
                setUseFastForwardAction(true)
                setUseRewindActionInCompactView(true)
                setUseFastForwardActionInCompactView(true)
                setUseRewindActionInCompactView(true)
                setUseFastForwardActionInCompactView(true)
            }

    }

    /**
     * Hides the notification.
     */
    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    /**
     * Shows the notification for the given player.
     * @param player The player instance for which the notification is shown.
     */
    fun showNotificationForPlayer(player: Player) {
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(private val controller: ListenableFuture<MediaController>) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.get().sessionActivity

        override fun getCurrentContentText(player: Player) =
            ""

        override fun getCurrentContentTitle(player: Player) =
            controller.get().mediaMetadata.title.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = controller.get().mediaMetadata.artworkUri
            return if (currentIconUri != iconUri || currentBitmap == null) {

                // Cache the bitmaqp for the current song so that successive calls to
                // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
                currentIconUri = iconUri
                serviceScope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it.toString().toInt())
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                currentBitmap
            }
        }

        private fun resolveUriAsBitmap(id: Int): Bitmap? {
            return BitmapFactory.decodeResource(MainActivity.applicationContext.resources, id);
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