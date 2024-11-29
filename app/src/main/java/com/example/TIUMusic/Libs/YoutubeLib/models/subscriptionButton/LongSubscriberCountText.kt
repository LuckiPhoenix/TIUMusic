package com.example.TIUMusic.Libs.YoutubeLib.models.subscriptionButton


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LongSubscriberCountText(
    @SerialName("runs")
    val runs: List<Run>
)