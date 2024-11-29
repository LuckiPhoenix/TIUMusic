package com.example.TIUMusic.Libs.YoutubeLib.pages

import com.example.TIUMusic.Libs.YoutubeLib.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)
