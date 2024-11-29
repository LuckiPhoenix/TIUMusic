package com.example.TIUMusic.Libs.YTMusicScrapper.models.response

import kotlinx.serialization.Serializable

@Serializable
data class CreatePlaylistResponse(
    val playlistId: String,
)