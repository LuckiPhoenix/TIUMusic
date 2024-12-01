package com.example.TIUMusic.SongData

import com.example.TIUMusic.Libs.YoutubeLib.getYoutubeHDThumbnail

data class MusicItem(
    val videoId: String,
    val title: String,
    val artist: String,
    val imageUrl: String,
    val type: Int, // 0: Song, 1: Playlist, 2: Album, 3, Artist
) {
    fun getHDThumbnail() : String {
        return getYoutubeHDThumbnail(videoId);
    }
}

data class NewReleaseCard(
    val type: String,
    val musicItem: MusicItem
)
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