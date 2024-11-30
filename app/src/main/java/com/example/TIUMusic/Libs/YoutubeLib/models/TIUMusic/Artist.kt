package com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic

import com.google.gson.annotations.SerializedName

data class Artist(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String
)