package com.example.TIUMusic.Libs.YoutubeLib.models

data class SearchSuggestions(
    val queries: List<String>,
    val recommendedItems: List<YTItem>,
)
