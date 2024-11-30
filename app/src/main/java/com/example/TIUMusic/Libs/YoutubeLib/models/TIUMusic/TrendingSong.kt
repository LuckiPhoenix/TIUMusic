package com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic

import com.example.TIUMusic.Libs.YoutubeLib.models.Album
import com.example.TIUMusic.Libs.YoutubeLib.models.Artist

data class TrendingSong(
    val album: Album?,
    val artists: List<Artist>?,
    val thumbnail: String,
    val title: String,
    val videoId: String,
)
