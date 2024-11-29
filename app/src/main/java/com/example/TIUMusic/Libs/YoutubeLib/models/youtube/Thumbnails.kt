package com.example.TIUMusic.Libs.YTMusicScrapper.models.youtube


import com.example.TIUMusic.Libs.YTMusicScrapper.models.Thumbnail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Thumbnails(
    @SerialName("thumbnails")
    val thumbnails: List<Thumbnail>? = null
)