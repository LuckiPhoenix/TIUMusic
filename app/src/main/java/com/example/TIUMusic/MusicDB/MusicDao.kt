package com.example.TIUMusic.MusicDB

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MusicDao {
    @Query("SELECT * FROM Song WHERE albumId = :albumId ORDER BY albumTrack ASC")
    suspend fun getSongsInAlbumById(albumId: Int) : List<Song>;

    @Query("SELECT * FROM Song INNER JOIN Album ON Album.id=Song.albumId WHERE Album.title = :title ORDER BY albumTrack ASC")
    suspend fun getSongsInAlbumByAlbumTitle(title: String) : List<Song>;

    @Query("SELECT * FROM Album ORDER BY id ASC")
    suspend fun getAllAlbums() : List<Album>;

    @Query("SELECT * FROM Song ORDER BY id ASC")
    suspend fun getAllSongs() : List<Song>;

    @Query("SELECT * FROM GlobalPlaylist ORDER BY id ASC")
    suspend fun getAllPlaylist() : List<GlobalPlaylist>;

    @Query("SELECT * FROM Song WHERE id IN (:ids) ORDER BY id ASC")
    suspend fun getSongsByIds(ids : List<Int>) : List<Song>;

    @Query("SELECT * FROM Song WHERE id IN (SELECT id FROM SONG ORDER BY RANDOM() LIMIT :limit)")
    suspend fun getRandomSongs(limit : Int) : List<Song>;
}

class MusicRepository(private val musicDao : MusicDao) {
    suspend fun getAllAlbums() : List<Album> = musicDao.getAllAlbums();
    suspend fun getSongsInAlbumByAlbumTitle(title: String) : List<Song> = musicDao.getSongsInAlbumByAlbumTitle(title);
    suspend fun getAllSongs() : List<Song> = musicDao.getAllSongs();
    suspend fun getAllPlaylist() : List<GlobalPlaylist> = musicDao.getAllPlaylist();
    suspend fun getSongsByIds(ids : List<Int>) : List<Song> = musicDao.getSongsByIds(ids);
    suspend fun getSongsByAlbumId(albumId: Int) : List<Song> = musicDao.getSongsInAlbumById(albumId);
    suspend fun getRandomSongs(limit : Int) : List<Song> = musicDao.getRandomSongs(limit);
}