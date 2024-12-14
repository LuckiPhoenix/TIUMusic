package com.example.TIUMusic.SongData

import com.example.TIUMusic.Libs.YoutubeLib.YoutubeMetadata
import com.example.TIUMusic.Libs.YoutubeLib.getYoutubeHDThumbnail
import com.example.TIUMusic.Libs.YoutubeLib.getYoutubeSmallThumbnail
import com.example.TIUMusic.Libs.YoutubeLib.models.Artist
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeContent

enum class MusicItemType {
    Song,
    Album,
    GlobalPlaylist,
    UserPlaylist,
    Artist
}

data class MusicItem(
    val videoId: String,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val type: MusicItemType, // 0: Song, 1: Playlist, 2: Album, 3: Artist
    val imageRId : Int? = null,
    val playlistId : String = "",
    val browseId : String = "",
    val fallbackThumbnail : String = "",
    val playlistSongsIds : String = "",
) {
    fun getHDThumbnail() : String = getYoutubeHDThumbnail(videoId);
    fun getSmallThumbnail() : String = getYoutubeSmallThumbnail(videoId);
    companion object {
        val EMPTY = MusicItem("", "", "", "", MusicItemType.Song);
    }
}

data class AlbumItem(
    val title: String,
    val description: String?,
    val artist: String,
    val year: Int?,
    val imageUrl: String,
    val duration: String?,
    val songs: List<MusicItem>
)

data class NewReleaseCard(
    val type: String,
    val musicItem: MusicItem
)

data class MoodItem(
    val title: String,
    val color: Int,
    val params: String
)


fun toMusicItemsList(list : List<HomeContent?>) : List<MusicItem> {
    val musicItems = mutableListOf<MusicItem>();
    for (item in list) {
        if (item != null)
            musicItems.add(fromHomeContent(item, item.browseId == null && item.playlistId == null));
    }
    return musicItems;
}

fun fromHomeContent(item : HomeContent, useHDImage: Boolean) : MusicItem {
    var type = MusicItemType.Song
    var id = ""
    if(item.browseId != null){
        type = MusicItemType.Album
        id = item.browseId
    }
    if(item.playlistId != null){
        type = MusicItemType.GlobalPlaylist
        id = item.playlistId
    }
    if(item.videoId != null && item.videoId != ""){
        type = MusicItemType.Song
        id = item.videoId
    }
    val thumbnail = item.thumbnails.lastOrNull();
    var thumbnailUrl = "";
    if (thumbnail != null) {
        if (item.browseId == null && item.playlistId == null && item.videoId != null)
            thumbnailUrl = getYoutubeHDThumbnail(item.videoId);
        else
            thumbnailUrl = thumbnail.url
    }
    return MusicItem(
        videoId = item.videoId ?: "",
        browseId = item.browseId ?: "",
        playlistId = item.playlistId ?: "",
        title = item.title,
        artist = item.artists?.firstOrNull()?.name ?: "",
        imageUrl = thumbnailUrl,
        type = type,
        fallbackThumbnail = thumbnail?.url ?: ""
    )
}

//data class không hoàn thiện, Hải chỉnh lại theo file Json hoặc CSV của spotify API
// các file API/SDK logic viết tại folder này

/*
* list nhạc mẫu, không phải chính thức, chờ API trả về của Hải thì xoá
* */
