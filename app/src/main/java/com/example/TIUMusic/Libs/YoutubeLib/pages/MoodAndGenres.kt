package com.example.TIUMusic.Libs.YTMusicScrapper.pages

import com.example.TIUMusic.Libs.YTMusicScrapper.models.BrowseEndpoint
import com.example.TIUMusic.Libs.YTMusicScrapper.models.GridRenderer
import com.example.TIUMusic.Libs.YTMusicScrapper.models.MusicNavigationButtonRenderer
import com.example.TIUMusic.Libs.YTMusicScrapper.models.SectionListRenderer

data class MoodAndGenres(
    val title: String,
    val items: List<Item>,
) {
    data class Item(
        val title: String,
        val stripeColor: Long,
        val endpoint: BrowseEndpoint,
    )

    companion object {
        fun fromSectionListRendererContent(content: SectionListRenderer.Content): MoodAndGenres? {
            return MoodAndGenres(
                title = content.gridRenderer?.header?.gridHeaderRenderer?.title?.runs?.firstOrNull()?.text ?: return null,
                items = content.gridRenderer.items
                    .mapNotNull(GridRenderer.Item::musicNavigationButtonRenderer)
                    .mapNotNull(Companion::fromMusicNavigationButtonRenderer)
            )
        }

        fun fromMusicNavigationButtonRenderer(renderer: MusicNavigationButtonRenderer): Item? {
            return Item(
                title = renderer.buttonText.runs?.firstOrNull()?.text ?: return null,
                stripeColor = renderer.solid?.leftStripeColor ?: return null,
                endpoint = renderer.clickCommand.browseEndpoint ?: return null,
            )
        }
    }
}
