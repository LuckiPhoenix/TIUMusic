package com.example.TIUMusic.Libs.YoutubeLib.models

sealed class MediaType {
    data object Song : MediaType()
    data object Video : MediaType()
}