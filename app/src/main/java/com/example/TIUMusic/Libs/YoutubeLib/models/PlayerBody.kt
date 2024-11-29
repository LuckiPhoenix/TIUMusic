package com.example.TIUMusic.Libs.YoutubeLib.models

import com.example.TIUMusic.Libs.YTMusicScrapper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class PlayerBody(
    val context: Context,
    val videoId: String,
    val playlistId: String?,
    val cpn: String?,
    val param: String? = "8AUB",
)