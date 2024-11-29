package com.example.TIUMusic.Libs.YoutubeLib.models.response

import com.example.TIUMusic.Libs.YoutubeLib.models.PlaylistPanelRenderer
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
