package com.example.TIUMusic.MusicDB

import android.content.Context
import android.provider.Settings.Global
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class, Album::class, GlobalPlaylist::class], version = 1)
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance;
                return instance;
            }
        }
    }
}
