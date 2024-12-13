package com.example.TIUMusic.MusicDB

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MusicDao {
    @Query("SELECT * FROM Song WHERE albumId = :albumId ORDER BY albumTrack ASC")
    fun getSongsInAlbumById(albumId: Int) : List<Song>;

    @Query("SELECT * FROM Song INNER JOIN Album ON Album.id=Song.albumId WHERE Album.title = :title ORDER BY albumTrack ASC")
    fun getSongsInAlbumByTitle(title: String) : List<Song>;

    @Query("SELECT * FROM Album ORDER BY id ASC")
    fun getAllAlbums() : List<Album>;

    @Query("SELECT * FROM Song ORDER BY id ASC")
    fun getAllSongs() : List<Song>;
}

