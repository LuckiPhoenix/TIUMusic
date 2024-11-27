package com.example.TIUMusic.Libs.YoutubeLib.models

import kotlinx.serialization.Serializable


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
)

@Serializable
data class Content(
    val sectionListRenderer: SectionListRenderer
)

@Serializable
data class SectionListRenderer(
    val contents: List<ContentItem>
)

@Serializable
data class ContentItem(
    val musicCardShelfRenderer: MusicCardShelfRenderer? = null
)

@Serializable
data class MusicCardShelfRenderer(
    val title: Title,
)

@Serializable
data class Title(
    val runs: List<Run>
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
data class VideoInfo(
    val videoId: String?,
    val title: String
)



