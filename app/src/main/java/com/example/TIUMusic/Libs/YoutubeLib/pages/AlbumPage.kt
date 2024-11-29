package com.example.TIUMusic.Libs.YoutubeLib.pages

import com.example.TIUMusic.Libs.YoutubeLib.models.Album
import com.example.TIUMusic.Libs.YoutubeLib.models.AlbumItem
import com.example.TIUMusic.Libs.YoutubeLib.models.Artist
import com.example.TIUMusic.Libs.YoutubeLib.models.MusicResponsiveListItemRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.SongItem
import com.example.TIUMusic.Libs.YoutubeLib.models.Thumbnail
import com.example.TIUMusic.Libs.YoutubeLib.models.Thumbnails
import com.example.TIUMusic.Libs.YoutubeLib.models.oddElements
import com.example.TIUMusic.Libs.YoutubeLib.utils.parseTime

data class AlbumPage(
    val album: AlbumItem,
    val songs: List<SongItem>,
    val description: String?,
    val thumbnails: Thumbnails?,
    val duration: String?,
) {
    companion object {
        fun fromMusicResponsiveListItemRenderer(renderer: MusicResponsiveListItemRenderer?, album: AlbumItem): SongItem? {
            if (renderer == null) return null
            else {
                return SongItem(
                    id = renderer.playlistItemData?.videoId ?: return null,
                    title = renderer.flexColumns.firstOrNull()
                        ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs
                        ?.firstOrNull()?.text ?: return null,
                    artists = renderer.flexColumns.getOrNull(1)?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.oddElements()
                        ?.map {
                            Artist(
                                name = it.text,
                                id = it.navigationEndpoint?.browseEndpoint?.browseId
                            )
                        } ?: album.artists ?: emptyList(),
                    album = Album(
                        name = album.title,
                        id = album.id
                    ),
                    duration = renderer.fixedColumns?.firstOrNull()
                        ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()
                        ?.text?.parseTime() ?: return null,
                    thumbnail = album.thumbnail,
                    thumbnails = Thumbnails(
                        thumbnails = listOf(Thumbnail(
                            url = album.thumbnail,
                            width = 544,
                            height = 544
                        ))
                    ),
                    explicit = renderer.badges?.find {
                        it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                    } != null
                )
            }
        }
    }
}