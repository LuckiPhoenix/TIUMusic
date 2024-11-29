package com.example.TIUMusic.Libs.YTMusicScrapper.models.body

import com.example.TIUMusic.Libs.YTMusicScrapper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class NotificationBody(
    val context: Context,
    val notificationsMenuRequestType: String = "NOTIFICATIONS_MENU_REQUEST_TYPE_INBOX",
) {
}