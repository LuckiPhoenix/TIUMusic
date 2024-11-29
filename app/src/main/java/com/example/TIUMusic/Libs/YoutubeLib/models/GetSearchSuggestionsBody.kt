package com.example.TIUMusic.Libs.YoutubeLib.models

import com.example.TIUMusic.Libs.YTMusicScrapper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsBody(
    val context: Context,
    val input: String,
)