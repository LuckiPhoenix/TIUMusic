package com.example.TIUMusic.Libs.YTMusicScrapper.models

sealed class MediaType {
    data object Song : MediaType()
    data object Video : MediaType()
}