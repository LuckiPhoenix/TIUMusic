package com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic

import com.example.TIUMusic.SongData.MusicItem

data class PlaylistBrowse(
    val id: String,
    val tracks : List<MusicItem>,
    val originalTrack : MusicItem?,
)