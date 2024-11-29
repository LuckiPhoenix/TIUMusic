package com.example.TIUMusic.Libs.YTMusicScrapper.models.response

import com.example.TIUMusic.Libs.YTMusicScrapper.models.SearchSuggestionsSectionRenderer
import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsResponse(
    val contents: List<Content>?,
) {
    @Serializable
    data class Content(
        val searchSuggestionsSectionRenderer: SearchSuggestionsSectionRenderer,
    )
}
