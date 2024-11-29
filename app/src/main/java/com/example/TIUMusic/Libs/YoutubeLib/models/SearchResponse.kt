package com.example.TIUMusic.Libs.YoutubeLib.models.old

import com.example.TIUMusic.Libs.YoutubeLib.models.BrowseEndpoint
import com.example.TIUMusic.Libs.YoutubeLib.models.WatchEndpoint
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames


@Serializable
data class SearchResponse(
    val contents: Contents
)

@Serializable
data class Contents(
    val tabbedSearchResultsRenderer: TabbedSearchResultsRenderer
)

@Serializable
data class TabbedSearchResultsRenderer(
    val tabs: List<Tab>
)

@Serializable
data class Tab(
    val tabRenderer: TabRenderer
)

@Serializable
data class TabRenderer(
    val content: Content
){
    @Serializable
    data class Content(
        val sectionListRenderer: SectionListRenderer
    )
}

@Serializable
data class SectionListRenderer(
    val contents: List<ContentItem>
)

@Serializable
data class ContentItem(
    val musicCardShelfRender: MusicCardShelfRender? = null,
    val musicShelfRenderer: MusicShelfRenderer? = null
)

@Serializable
data class MusicCardShelfRender(
    val title: Runs
)

@Serializable
data class MusicShelfRenderer(
    val title: Runs,
    val contents: List<Content>? = null
){
    @Serializable
    data class Content(
        val musicResponsiveListItemRenderer: MusicResponsiveListItemRenderer?
    )
}

@Serializable
data class MusicResponsiveListItemRenderer(
    val flexColumns: List<FlexColumns>
)

@Serializable
data class FlexColumns(
    @JsonNames("musicResponsiveListItemFixedColumnRenderer")
    val musicResponsiveListItemFlexColumnRenderer: MusicResponsiveListItemFlexColumnRenderer)

@Serializable
data class MusicResponsiveListItemFlexColumnRenderer(
    val text: Runs?
)

@Serializable
data class Runs(
    val runs: List<Run>?,
)

@Serializable
data class Run(
    val text: String,
    val navigationEndpoint: NavigationEndpoint? = null
)

@Serializable
data class NavigationEndpoint(
    val watchEndpoint: WatchEndpoint? = null,
    val browseEndpoint: BrowseEndpoint? = null
)

// Data class để lưu trữ video ID
@Serializable
data class SearchingInfo(
    val title: String?,
    val videoId: String?,
    val artist: String?,
    val artistId: String?
)



