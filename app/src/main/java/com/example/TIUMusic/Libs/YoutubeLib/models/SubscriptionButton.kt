package com.example.TIUMusic.Libs.YTMusicScrapper.models

import com.example.TIUMusic.Libs.YTMusicScrapper.models.subscriptionButton.SubscribeButtonRenderer
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionButton(
    val subscribeButtonRenderer: SubscribeButtonRenderer,
)