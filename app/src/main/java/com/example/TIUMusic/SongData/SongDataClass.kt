package com.example.TIUMusic.SongData

import com.example.TIUMusic.Libs.YoutubeLib.getYoutubeHDThumbnail

data class MusicItem(
    val videoId: String,
    val title: String,
    val artist: String,
    val imageUrl: String
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
        videoId = "p1",
        title = "Trending Now",
        artist = "Playlist • Updated Daily",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p2",
        title = "Global Top 50",
        artist = "Playlist • 2.5M likes",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p3",
        title = "Viral Hits",
        artist = "Playlist • Fresh Daily Mix",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p4",
        title = "New Music Friday",
        artist = "Playlist • Weekly Update",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    )
)

fun getRecentItems(): List<MusicItem> = listOf(
    MusicItem(
        videoId = "p5",
        title = "Your Top 2023",
        artist = "Playlist • Personal Mix",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p6",
        title = "Recently Played",
        artist = "Playlist • Updated Daily",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p7",
        title = "On Repeat",
        artist = "Playlist • Your Favorites",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    )
)

fun getBasedOnRecent(): List<MusicItem> = listOf(
    MusicItem(
        videoId = "p8",
        title = "Discover Weekly",
        artist = "Playlist • Made For You",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p9",
        title = "Daily Mix 1",
        artist = "Playlist • Based on your listening",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p10",
        title = "Recommended Radio",
        artist = "Playlist • Similar to your taste",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    )
)

fun getPlaylists(): List<MusicItem> = listOf(
    MusicItem(
        videoId = "p11",
        title = "Chill Vibes",
        artist = "Playlist • Perfect for relaxing",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p12",
        title = "Workout Essentials",
        artist = "Playlist • High energy mix",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p13",
        title = "Study Focus",
        artist = "Playlist • Concentration boost",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    ),
    MusicItem(
        videoId = "p14",
        title = "Sleep Sounds",
        artist = "Playlist • Calm & peaceful",
        imageUrl = "https://i1.sndcdn.com/artworks-BWJgBLZhC32e-0-t500x500.jpg"
    )
)