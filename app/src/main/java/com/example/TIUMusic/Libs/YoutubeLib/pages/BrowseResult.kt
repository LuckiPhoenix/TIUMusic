package com.example.TIUMusic.Libs.YoutubeLib.pages

import com.example.TIUMusic.Libs.YoutubeLib.models.YTItem

data class BrowseResult(
    val title: String?,
    val items: List<Item>,
) {
    data class Item(
        val title: String?,
        val items: List<YTItem>,
    )
}
