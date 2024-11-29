package com.example.TIUMusic.Libs.YTMusicScrapper.models.response

import com.example.TIUMusic.Libs.YTMusicScrapper.models.PlaylistPanelRenderer
import kotlinx.serialization.Serializable

@Serializable
data class GetQueueResponse(
    val queueDatas: List<QueueData>,
) {
    @Serializable
    data class QueueData(
        val content: PlaylistPanelRenderer.Content,
    )
}
