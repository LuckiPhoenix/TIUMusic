package com.example.TIUMusic.Libs.YTMusicScrapper.pages

import com.example.TIUMusic.Libs.YTMusicScrapper.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)
