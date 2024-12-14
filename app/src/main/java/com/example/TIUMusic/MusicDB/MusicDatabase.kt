package com.example.TIUMusic.MusicDB

import android.content.Context
import android.provider.Settings.Global
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class, Album::class, GlobalPlaylist::class], version = 1, exportSchema = true)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicDao() : MusicDao

    companion object {
        @Volatile
        private var INSTANCE : MusicDatabase? = null;

        fun getDatabase(context : Context) : MusicDatabase {
            if (INSTANCE != null)
                return INSTANCE as MusicDatabase;

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "music_database"
                )
                    .createFromAsset("database/MusicDB.db")
                    .allowMainThreadQueries()
                    .build()
                instance.openHelper.readableDatabase.let {
                    val test = it.query("SELECT * FROM Song");
                    Log.d("MusicDB", "Database Found");
                }
                INSTANCE = instance;
                return instance;
            }
        }
    }
}

class MusicRepository(private val musicDao : MusicDao) {
    val readAllData : List<Song> = musicDao.getAllSongs();
    val albums : List<Album> = musicDao.getAllAlbums();
    val playlist : List<GlobalPlaylist> = musicDao.getAllPlaylist();

    fun getSongsByIds(ids : List<Int>) : List<Song> = musicDao.getSongsByIds(ids);
    fun getSongsByAlbumId(albumId: Int) : List<Song> = musicDao.getSongsInAlbumById(albumId);
    fun getRandomSongs(limit : Int) : List<Song> = musicDao.getRandomSongs(limit);
}