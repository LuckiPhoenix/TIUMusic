package com.example.TIUMusic.Libs.YTMusicScrapper.pages

import com.example.TIUMusic.Libs.YTMusicScrapper.models.PlaylistItem
import com.example.TIUMusic.Libs.YTMusicScrapper.models.VideoItem

data class ExplorePage(
    val released: List<PlaylistItem>,
    val musicVideo: List<VideoItem>,
)
