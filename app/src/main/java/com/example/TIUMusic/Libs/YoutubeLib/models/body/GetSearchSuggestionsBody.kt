package com.example.TIUMusic.Libs.YoutubeLib.models.body

import com.example.TIUMusic.Libs.YoutubeLib.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsBody(
    val context: Context,
    val input: String,
)
