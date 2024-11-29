package com.example.TIUMusic.Libs.YoutubeLib.models

import com.example.TIUMusic.Libs.YoutubeLib.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class SearchBody(
    val context: Context,
    val query: String?,
    val params: String?,
)