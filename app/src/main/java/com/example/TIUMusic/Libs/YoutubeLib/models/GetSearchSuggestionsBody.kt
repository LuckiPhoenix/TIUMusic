package com.example.TIUMusic.Libs.YoutubeLib.models

import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsBody(
    val context: Context,
    val input: String,
)