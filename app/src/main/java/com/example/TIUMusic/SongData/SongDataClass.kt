package com.example.TIUMusic.SongData

import com.example.TIUMusic.Libs.YoutubeLib.YoutubeMetadata
import com.example.TIUMusic.Libs.YoutubeLib.getYoutubeHDThumbnail
import com.example.TIUMusic.Libs.YoutubeLib.getYoutubeSmallThumbnail
import com.example.TIUMusic.Libs.YoutubeLib.models.Artist
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeContent

data class MusicItem(
    val videoId: String,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val type: Int, // 0: Song, 1: Playlist, 2: Album, 3: Artist
    val playlistId : String = "",
    val browseId : String = "",
    val fallbackThumbnail : String = "",
) {
    fun getHDThumbnail() : String = getYoutubeHDThumbnail(videoId);
    fun getSmallThumbnail() : String = getYoutubeSmallThumbnail(videoId);
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
    val list: List<MusicItem>
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
    var type = 0
    var id = ""
    if(item.browseId != null){
        type = 2
        id = item.browseId
    }
    if(item.playlistId != null){
        type = 1
        id = item.playlistId
    }
    if(item.videoId != null && item.videoId != ""){
        type = 0
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


fun getTopPicks(): List<MusicItem> = listOf(
    MusicItem(
         "p1",
        title = "Trending Now",
        artist = "Playlist • Updated Daily",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
        type = 1
    ),
    MusicItem(
        "p2",
        title = "Global Top 50",
        artist = "Playlist • 2.5M likes",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
        type = 1
    ),
    MusicItem(
        "p3",
        title = "Viral Hits",
        artist = "Playlist • Fresh Daily Mix",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
        type = 1
    ),
    MusicItem(
        "p4",
        title = "New Music Friday",
        artist = "Playlist • Weekly Update",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg",
        type = 1
    )
)