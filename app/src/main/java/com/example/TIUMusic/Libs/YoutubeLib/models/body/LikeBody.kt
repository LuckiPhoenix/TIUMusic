package com.example.TIUMusic.Libs.YoutubeLib.models.body

import com.example.TIUMusic.Libs.YoutubeLib.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class LikeBody(
    val context: Context,
    val target: Target,
) {
    @Serializable
    data class Target(
        val videoId: String
    )
}