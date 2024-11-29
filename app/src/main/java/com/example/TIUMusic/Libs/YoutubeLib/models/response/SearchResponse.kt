package com.example.TIUMusic.Libs.YTMusicScrapper.models.response

import com.example.TIUMusic.Libs.YTMusicScrapper.models.Continuation
import com.example.TIUMusic.Libs.YTMusicScrapper.models.MusicResponsiveListItemRenderer
import com.example.TIUMusic.Libs.YTMusicScrapper.models.MusicShelfRenderer
import com.example.TIUMusic.Libs.YTMusicScrapper.models.Tabs
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val contents: Contents?,
    val continuationContents: ContinuationContents?,
) {
    @Serializable
    data class Contents(
        val tabbedSearchResultsRenderer: Tabs?,
    )

    @Serializable
    data class ContinuationContents(
        val musicShelfContinuation: MusicShelfContinuation,
    ) {
        @Serializable
        data class MusicShelfContinuation(
            val contents: List<Content>,
            val continuations: List<Continuation>?,
        ) {
            @Serializable
            data class Content(
                val musicResponsiveListItemRenderer: MusicResponsiveListItemRenderer?,
                val musicMultiRowListItemRenderer: MusicShelfRenderer.Content.MusicMultiRowListItemRenderer?,
            )
        }
    }
}
