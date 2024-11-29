package com.example.TIUMusic.Libs.YoutubeLib.models

import com.example.TIUMusic.Libs.YoutubeLib.models.subscriptionButton.SubscribeButtonRenderer
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionButton(
    val subscribeButtonRenderer: SubscribeButtonRenderer,
)