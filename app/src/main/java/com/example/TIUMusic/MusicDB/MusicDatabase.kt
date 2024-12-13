package com.example.TIUMusic.MusicDB

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 1, exportSchema = true)
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
    val readAllData : LiveData<List<Song>> = musicDao.readAllData();
}