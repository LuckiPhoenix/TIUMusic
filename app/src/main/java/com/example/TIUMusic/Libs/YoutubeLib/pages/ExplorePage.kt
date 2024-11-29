package com.example.TIUMusic.Libs.YoutubeLib.pages

import com.example.TIUMusic.Libs.YoutubeLib.models.PlaylistItem
import com.example.TIUMusic.Libs.YoutubeLib.models.VideoItem

data class ExplorePage(
    val released: List<PlaylistItem>,
    val musicVideo: List<VideoItem>,
)
