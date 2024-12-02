package com.example.TIUMusic.Libs.YoutubeLib.parser

import android.util.Log
import com.example.TIUMusic.Libs.YoutubeLib.models.Artist
import com.example.TIUMusic.Libs.YoutubeLib.models.MusicResponsiveListItemRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.MusicShelfRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.SongItem
import com.example.TIUMusic.Libs.YoutubeLib.models.getContinuation
import com.example.TIUMusic.Libs.YoutubeLib.models.response.BrowseResponse
import com.example.TIUMusic.Libs.YoutubeLib.models.response.LikeStatus

fun BrowseResponse.fromPlaylistToTrack(): List<SongItem> =
    (
        (this.contents
            ?.singleColumnBrowseResultsRenderer?.tabs
            ?: this.contents?.twoColumnBrowseResultsRenderer?.tabs
            )?.firstOrNull()
            ?.tabRenderer
            ?.content
            ?.sectionListRenderer
            ?.contents
            ?.firstOrNull()
            ?.musicPlaylistShelfRenderer
            ?.contents
            ?: this.contents
                ?.twoColumnBrowseResultsRenderer
                ?.secondaryContents
                ?.sectionListRenderer
                ?.contents
                ?.firstOrNull()
                ?.musicPlaylistShelfRenderer
                ?.contents
    )?.mapNotNull { content ->
        content.toSongItem()
    } ?: emptyList()

fun BrowseResponse.fromPlaylistToTrackWithSetVideoId(): List<Pair<SongItem, String>> =
    (
        (this.contents
            ?.singleColumnBrowseResultsRenderer?.tabs
            ?: this.contents?.twoColumnBrowseResultsRenderer?.tabs
            )?.firstOrNull()
            ?.tabRenderer
            ?.content
            ?.sectionListRenderer
            ?.contents
            ?.firstOrNull()
            ?.musicPlaylistShelfRenderer
            ?.contents
            ?: this.contents
                ?.twoColumnBrowseResultsRenderer
                ?.secondaryContents
                ?.sectionListRenderer
                ?.contents
                ?.firstOrNull()
                ?.musicPlaylistShelfRenderer
                ?.contents
        )?.mapNotNull { content ->
            Pair(
                content.toSongItem() ?: return@mapNotNull null,
                content.toPlaylistItemData()?.playlistSetVideoId ?: return@mapNotNull null,
            )
        } ?: emptyList()

fun BrowseResponse.fromPlaylistContinuationToTracks(): List<SongItem> =
    (this.continuationContents
        ?.musicPlaylistShelfContinuation
        ?.contents
        ?: this.continuationContents
            ?.sectionListContinuation
            ?.contents
            ?.firstOrNull()
            ?.musicShelfRenderer
            ?.contents)
        ?.mapNotNull { contents ->
            contents.toSongItem()
        } ?: emptyList()

fun BrowseResponse.fromPlaylistContinuationToTrackWithSetVideoId(): List<Pair<SongItem, String>> =
    (this.continuationContents
        ?.musicPlaylistShelfContinuation
        ?.contents
    ?: this.continuationContents
        ?.sectionListContinuation
        ?.contents
        ?.firstOrNull()
        ?.musicShelfRenderer
        ?.contents)
        ?.mapNotNull { contents ->
            Pair(
                contents.toSongItem() ?: return@mapNotNull null,
                contents.toPlaylistItemData()?.playlistSetVideoId ?: return@mapNotNull null,
            )
        } ?: emptyList()

fun BrowseResponse.getPlaylistContinuation(): String? =
    this.contents
        ?.singleColumnBrowseResultsRenderer
        ?.tabs
        ?.firstOrNull()
        ?.tabRenderer
        ?.content
        ?.sectionListRenderer
        ?.continuations
        ?.getContinuation()
    ?: this.contents
        ?.twoColumnBrowseResultsRenderer
        ?.secondaryContents
        ?.sectionListRenderer
        ?.continuations
        ?.getContinuation()

fun BrowseResponse.getContinuePlaylistContinuation(): String? =
    this.continuationContents
        ?.musicPlaylistShelfContinuation
        ?.continuations
        ?.getContinuation()

fun MusicShelfRenderer.Content.toPlaylistItemData(): MusicResponsiveListItemRenderer.PlaylistItemData? =
    this.musicResponsiveListItemRenderer?.playlistItemData

fun MusicShelfRenderer.Content.toSongItem(): SongItem? {
    val flexColumns = this.musicResponsiveListItemRenderer?.flexColumns
    val fixedColumns = this.musicResponsiveListItemRenderer?.fixedColumns
    val menu = this.musicResponsiveListItemRenderer?.menu
    val artistRun =
        flexColumns?.getOrNull(1)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull();
    var artist : Artist? = null;
    if (artistRun != null) {
        artist = Artist(
            artistRun.text,
            artistRun.navigationEndpoint?.browseEndpoint?.browseId
        )
    }
    return SongItem(
        id =
            flexColumns
                ?.firstOrNull()
                ?.musicResponsiveListItemFlexColumnRenderer
                ?.text
                ?.runs
                ?.firstOrNull()
                ?.navigationEndpoint
                ?.watchEndpoint
                ?.videoId ?: return null,
        title =
            flexColumns
                .firstOrNull()
                ?.musicResponsiveListItemFlexColumnRenderer
                ?.text
                ?.runs
                ?.firstOrNull()
                ?.text ?: return null,
        artists = if (artist != null) listOf(artist) else listOf(),
        album =
            flexColumns.find {
                it.musicResponsiveListItemFlexColumnRenderer.isAlbum()
            }?.musicResponsiveListItemFlexColumnRenderer?.toAlbum(),
        duration =
            fixedColumns
                ?.firstOrNull()
                ?.musicResponsiveListItemFlexColumnRenderer
                ?.text
                ?.runs
                ?.firstOrNull()
                ?.text
                .toDurationSeconds(),
        thumbnail =
            this.musicResponsiveListItemRenderer
                ?.thumbnail
                ?.musicThumbnailRenderer
                ?.getThumbnailUrl() ?: "",
        endpoint = flexColumns
            .first()
            .musicResponsiveListItemFlexColumnRenderer
            .text
            ?.runs
            ?.firstOrNull()
            ?.navigationEndpoint
            ?.watchEndpoint,
        explicit =
            this.musicResponsiveListItemRenderer?.badges?.toSongBadges()?.contains(
                SongItem.SongBadges.Explicit,
            ) ?: false,
        thumbnails =
            this.musicResponsiveListItemRenderer
                ?.thumbnail
                ?.musicThumbnailRenderer
                ?.thumbnail,
        likeStatus =
            menu
                ?.menuRenderer
                ?.topLevelButtons
                ?.firstOrNull()
                ?.likeButtonRenderer
                ?.toLikeStatus()
                ?: LikeStatus.INDIFFERENT,
        badges = this.musicResponsiveListItemRenderer?.badges?.toSongBadges(),
    )
}

/**
 * Check if the browse response has reload params, if true, this has the suggestion tracks
 */
fun BrowseResponse.hasReloadParams(): Boolean =
    this.continuationContents
        ?.sectionListContinuation
        ?.contents
        ?.firstOrNull()
        ?.musicShelfRenderer
        ?.continuations
        ?.firstOrNull()
        ?.reloadContinuationData
        ?.continuation != null

fun BrowseResponse.getReloadParams(): String? =
    this.continuationContents
        ?.sectionListContinuation
        ?.contents
        ?.firstOrNull()
        ?.musicShelfRenderer
        ?.continuations
        ?.firstOrNull()
        ?.reloadContinuationData
        ?.continuation

fun BrowseResponse.getSuggestionSongItems(): List<SongItem> =
    if (hasReloadParams()) {
        this.continuationContents
            ?.sectionListContinuation
            ?.contents
            ?.firstOrNull()
            ?.musicShelfRenderer
            ?.contents
            ?.mapNotNull { content ->
                content.toSongItem()
            } ?: emptyList()
    } else {
        emptyList()
    }