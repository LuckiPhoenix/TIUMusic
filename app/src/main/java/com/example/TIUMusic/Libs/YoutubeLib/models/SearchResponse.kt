package com.example.TIUMusic.Libs.YoutubeLib.models

import kotlinx.serialization.Serializable

// Data class để lưu trữ video ID
@Serializable
data class SearchingInfo(
    val title: String?,
    val videoId: String?,
    val artist: String?,
    val artistId: String?
)



