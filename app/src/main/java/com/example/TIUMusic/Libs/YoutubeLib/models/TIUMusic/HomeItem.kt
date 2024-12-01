package com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic

import android.content.Context
import android.util.Log
import com.example.TIUMusic.Libs.YoutubeLib.models.Album
import com.example.TIUMusic.Libs.YoutubeLib.models.Artist
import com.example.TIUMusic.Libs.YoutubeLib.models.MusicResponsiveListItemRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.Run
import com.example.TIUMusic.Libs.YoutubeLib.models.Thumbnail
import com.example.TIUMusic.R
import com.google.gson.annotations.SerializedName

data class HomeItem(
    val contents: List<HomeContent?>,
    val title: String,
    val subtitle: String? = null,
    val thumbnail: List<Thumbnail>? = null,
    val channelId: String? = null,
)

data class HomeContent(
    @SerializedName("album")
    val album: Album?,
    @SerializedName("artists")
    val artists: List<Artist>?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("isExplicit")
    val isExplicit: Boolean?,
    @SerializedName("playlistId")
    val playlistId: String?,
    @SerializedName("browseId")
    val browseId: String?,
    @SerializedName("thumbnails")
    val thumbnails: List<Thumbnail>,
    @SerializedName("title")
    val title: String,
    @SerializedName("videoId")
    val videoId: String?,
    @SerializedName("views")
    val views: String?,
    @SerializedName("durationSeconds")
    val durationSeconds: Int? = null,
    val radio: String? = null,
)

fun parseSongArtists(
    data: MusicResponsiveListItemRenderer,
    index: Int,
    context: Context,
): List<Artist>? {
    val flexItem = getFlexColumnItem(data, index)
    return if (flexItem == null) {
        null
    } else {
        val runs = flexItem.text?.runs
        runs?.let { parseSongArtistsRuns(it, context) }
    }
}

fun getFlexColumnItem(
    data: MusicResponsiveListItemRenderer,
    index: Int,
): MusicResponsiveListItemRenderer.FlexColumn.MusicResponsiveListItemFlexColumnRenderer? =
    if (data.flexColumns.size <= index ||
        data.flexColumns[index].musicResponsiveListItemFlexColumnRenderer.text == null ||
        data.flexColumns[index]
            .musicResponsiveListItemFlexColumnRenderer.text
            ?.runs == null
    ) {
        null
    } else {
        data.flexColumns[index].musicResponsiveListItemFlexColumnRenderer
    }

fun parseSongArtistsRuns(
    runs: List<Run>,
    context: Context,
): List<Artist> {
    val artists = mutableListOf<Artist>()
    for (i in 0..(runs.size / 2)) {
        if (runs[i * 2].navigationEndpoint?.browseEndpoint?.browseId != null) {
            artists.add(
                Artist(
                    name = runs[i * 2].text,
                    id = runs[i * 2].navigationEndpoint?.browseEndpoint?.browseId,
                ),
            )
        } else {
            if (!runs[i * 2].text.contains(
                    ("%1 views").removeRange(0..4),
                )
            ) {
                artists.add(Artist(name = runs[i * 2].text, id = null))
            }
        }
    }
    Log.d("artists_log", artists.toString())
    return artists
}


