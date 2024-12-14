package com.example.TIUMusic.MusicDB

import android.content.Context
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.TIUMusic.MainActivity
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.MusicItemType
import com.example.TIUMusic.Utils.nameToRID

@Entity(foreignKeys = [
    ForeignKey(
        entity = Album::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("albumId"),
        onDelete = ForeignKey.SET_NULL
    )
])
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val fileUri : String,
    val title: String,
    val artist: String,
    val imageUri: String,
    val albumId : Int?,
    val albumTrack : Int,
    val duration : Float,
    val releaseDate : String,
) {
    fun toMusicItem(context : Context) : MusicItem {
        return MusicItem(
            videoId = fileUri,
            title = title,
            artist = artist,
            imageUrl = imageUri,
            imageRId = nameToRID(imageUri, "raw", context),
            songId = id,
            type = MusicItemType.Song,
        )
    }
}

@Entity
data class Album(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val title: String,
    val artist: String,
    val imageUri: String,
    val releaseDate: String,
) {
    fun toMusicItem(context : Context) : MusicItem {
        return MusicItem(
            videoId = "",
            title = title,
            artist = artist,
            imageUrl = imageUri,
            imageRId = nameToRID(imageUri, "raw", context),
            playlistId = id.toString(),
            type = MusicItemType.Album,
        )
    }
}

@Entity
data class GlobalPlaylist(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val title : String,
    val artist: String,
    val imageUri: String,
    val songsIds : String,
) {
    fun toMusicItem(context : Context) : MusicItem {
        return MusicItem(
            videoId = "",
            title = title,
            artist = artist,
            imageUrl = imageUri,
            imageRId = nameToRID(imageUri, "raw", context),
            playlistId = songsIds,
            type = MusicItemType.GlobalPlaylist,
        )
    }
}