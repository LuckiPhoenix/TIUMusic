package com.example.TIUMusic.Libs.YoutubeLib.models

import kotlinx.serialization.Serializable

@Serializable
data class FormData (
    val selectedValues: List<String> = listOf("ZZ")
)