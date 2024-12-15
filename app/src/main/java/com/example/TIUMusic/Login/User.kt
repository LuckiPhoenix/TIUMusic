package com.example.TIUMusic.Login

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.TIUMusic.SongData.MusicItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    @TypeConverter
    fun fromPlaylistList(value: List<Playlist>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toPlaylistList(value: String): List<Playlist> {
        val type = object : TypeToken<List<Playlist>>() {}.type
        return Gson().fromJson(value, type)
    }
}
@Entity
data class User(
    var fullName: String,
    @PrimaryKey var email: String,
    var password: String,
    var profilePicture: String? = null, // New field for profile picture
    var playlists: MutableList<Playlist> = mutableListOf() // New field for playlists
)

@Entity(primaryKeys = arrayOf("userEmail", "songId"))
data class ListenHistory(
    var userEmail : String,
    var songId: Int,
    var listenCount : Int = 0,
    var lastListenDate : String,
)

// New Playlist data class
data class Playlist(
    val id: String, // Auto-generated ID
    var title: String, // Playlist title
    var picture: Int? = null, // Optional image for the playlist
    var songs: MutableList<Int> = mutableListOf(), // List of songs
    var description: String = "",
)

// MusicItem is reused from SongDataClass.kt