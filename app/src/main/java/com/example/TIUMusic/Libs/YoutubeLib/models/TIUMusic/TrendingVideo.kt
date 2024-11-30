package com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic

import com.example.TIUMusic.Libs.YoutubeLib.models.Artist

data class TrendingVideo(
    val artists : List<Artist>,
    val playlistId : String,
    val thumbnail : String,
    val title : String,
    val videoId : String,
    val views : String
)
