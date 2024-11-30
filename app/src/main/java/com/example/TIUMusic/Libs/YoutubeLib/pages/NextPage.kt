package com.example.TIUMusic.Libs.YoutubeLib.pages

import com.example.TIUMusic.Libs.YoutubeLib.models.Album
import com.example.TIUMusic.Libs.YoutubeLib.models.Artist
import com.example.TIUMusic.Libs.YoutubeLib.models.BrowseEndpoint
import com.example.TIUMusic.Libs.YoutubeLib.models.MusicResponsiveListItemRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.PlaylistPanelVideoRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.SongItem
import com.example.TIUMusic.Libs.YoutubeLib.models.WatchEndpoint
import com.example.TIUMusic.Libs.YoutubeLib.models.oddElements
import com.example.TIUMusic.Libs.YoutubeLib.models.splitBySeparator
import com.example.TIUMusic.Libs.YoutubeLib.utils.parseTime

data class NextResult(
    val title: String? = null,
    val items: List<SongItem>,
    val currentIndex: Int? = null,
    val lyricsEndpoint: BrowseEndpoint? = null,
    val relatedEndpoint: BrowseEndpoint? = null,
    val continuation: String?,
    val endpoint: WatchEndpoint, // current or continuation next endpoint
)

object NextPage {
    fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer): SongItem? {
        val videoId = renderer.playlistItemData?.videoId ?: return null
        val artistRuns = renderer.flexColumns.getOrNull(1)?.musicResponsiveListItemFlexColumnRenderer?.
            text?.runs?.oddElements() ?: return null
        val albumRuns = renderer.flexColumns.getOrNull(2)?.musicResponsiveListItemFlexColumnRenderer?.text
            ?.runs?.firstOrNull()
        return SongItem(
            id = videoId,
            title = renderer.flexColumns.firstOrNull()?.musicResponsiveListItemFlexColumnRenderer
                ?.text?.runs?.firstOrNull()?.text ?: return null,
            artists = artistRuns.map {
                Artist(
                    name = it.text,
                    id = it.navigationEndpoint?.browseEndpoint?.browseId
                )
            },
            album = albumRuns?.let {
                Album(
                    name = it.text,
                    id = it.navigationEndpoint?.browseEndpoint?.browseId ?: ""
                )
            },
            duration = renderer.fixedColumns?.firstOrNull()?.musicResponsiveListItemFlexColumnRenderer
                ?.text?.runs?.firstOrNull()?.text?.parseTime(),
            thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl() ?: "",
            explicit = false,
            endpoint = renderer.flexColumns.firstOrNull()?.musicResponsiveListItemFlexColumnRenderer
                ?.text?.runs?.firstOrNull()?.navigationEndpoint?.watchEndpoint,
            thumbnails = renderer.thumbnail?.musicThumbnailRenderer?.thumbnail
        )
    }
    fun fromPlaylistPanelVideoRenderer(renderer: PlaylistPanelVideoRenderer): SongItem? {
        val longByLineRuns = renderer.longBylineText?.runs?.splitBySeparator() ?: return null
        return SongItem(
            id = renderer.videoId ?: return null,
            title = renderer.title?.runs?.firstOrNull()?.text ?: return null,
            artists = longByLineRuns.firstOrNull()?.oddElements()?.map {
                Artist(
                    name = it.text,
                    id = it.navigationEndpoint?.browseEndpoint?.browseId
                )
            } ?: return null,
            album = longByLineRuns.getOrNull(1)?.firstOrNull()?.takeIf {
                it.navigationEndpoint?.browseEndpoint != null
            }?.let {
                Album(
                    name = it.text,
                    id = it.navigationEndpoint?.browseEndpoint?.browseId!!
                )
            },
            duration = renderer.lengthText?.runs?.firstOrNull()?.text?.parseTime() ?: return null,
            thumbnail = renderer.thumbnail.thumbnails.lastOrNull()?.url ?: return null,
            explicit = renderer.badges?.find {
                it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
            } != null,
            thumbnails = renderer.thumbnail
        )
    }
}
