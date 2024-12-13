package com.example.TIUMusic.MusicDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.TIUMusic.SongData.MusicItem

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val fileUri : String,
    val title: String,
    val artist: String,
    val imageUri: String,
) {
    fun toMusicItem() : MusicItem = MusicItem(
        videoId = fileUri,
        title = title,
        artist = artist,
        imageUrl = imageUri,
        type = 0,
    )
}