package com.example.TIUMusic.Libs.YTMusicScrapper.models.body

import com.example.TIUMusic.Libs.YTMusicScrapper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetTranscriptBody(
    val context: Context,
    val params: String,
)
