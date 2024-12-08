package com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("album_type")
    val albumType: String,
    @SerializedName("available_markets")
    val availableMarkets: List<String>,
    @SerializedName("href")
    val href: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("release_date_precision")
    val releaseDatePrecision: String,
    @SerializedName("total_tracks")
    val totalTracks: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("uri")
    val uri: String
)