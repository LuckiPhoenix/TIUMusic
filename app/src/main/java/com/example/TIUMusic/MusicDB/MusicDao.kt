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

    @Query("SELECT * FROM Song ORDER BY title ASC LIMIT :limit")
    suspend fun getSongsLimit(limit : Int) : List<Song>;

    @Query("SELECT * FROM Song WHERE id IN (:ids) ORDER BY id ASC")
    suspend fun getSongsByIds(ids : List<Int>) : List<Song>;

    @Query("SELECT * FROM Song ORDER BY releaseDate DESC LIMIT :limit")
    suspend fun getNewSongReleases(limit: Int) : List<Song>;

    @Query("SELECT * FROM Album ORDER BY releaseDate DESC LIMIT :limit")
    suspend fun getNewAlbumsReleases(limit: Int) : List<Album>;

    @Query("SELECT * FROM Album WHERE id IN (SELECT id FROM Album ORDER BY RANDOM() LIMIT :limit)")
    suspend fun getRandomAlbums(limit : Int) : List<Album>;

    @Query("SELECT * FROM GlobalPlaylist WHERE id IN (SELECT id FROM GlobalPlaylist ORDER BY RANDOM() LIMIT :limit)")
    suspend fun getRandomPlaylists(limit : Int) : List<GlobalPlaylist>;

    @Query("SELECT * FROM Song WHERE id IN (SELECT id FROM Song ORDER BY RANDOM() LIMIT :limit) ORDER BY RANDOM()")
    suspend fun getRandomSongs(limit : Int) : List<Song>;
}

class MusicRepository(private val musicDao : MusicDao) {
    suspend fun getAllAlbums() : List<Album> = musicDao.getAllAlbums();
    suspend fun getSongsInAlbumByAlbumTitle(title: String) : List<Song> = musicDao.getSongsInAlbumByAlbumTitle(title);
    suspend fun getAllSongs() : List<Song> = musicDao.getAllSongs();
    suspend fun getAllPlaylist() : List<GlobalPlaylist> = musicDao.getAllPlaylist();
    suspend fun getSongsLimit(limit : Int) : List<Song> = musicDao.getSongsLimit(limit);
    suspend fun getNewSongReleases(limit: Int) : List<Song> = musicDao.getNewSongReleases(limit);
    suspend fun getNewAlbumsReleases(limit: Int) : List<Album> = musicDao.getNewAlbumsReleases(limit);
    suspend fun getSongsByIds(ids : List<Int>) : List<Song> = musicDao.getSongsByIds(ids);
    suspend fun getSongsByAlbumId(albumId: Int) : List<Song> = musicDao.getSongsInAlbumById(albumId);
    suspend fun getRandomSongs(limit : Int) : List<Song> = musicDao.getRandomSongs(limit);
    suspend fun getRandomPlaylists(limit : Int) : List<GlobalPlaylist> = musicDao.getRandomPlaylists(limit);
    suspend fun getRandomAlbums(limit : Int) : List<Album> = musicDao.getRandomAlbums(limit);
}