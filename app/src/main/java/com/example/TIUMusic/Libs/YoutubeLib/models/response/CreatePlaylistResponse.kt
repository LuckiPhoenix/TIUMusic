package com.example.TIUMusic.Libs.YoutubeLib.models.response

import kotlinx.serialization.Serializable

@Serializable
data class CreatePlaylistResponse(
    val playlistId: String,
)