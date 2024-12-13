package com.example.TIUMusic.MusicDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MusicDao {
    @Upsert
    suspend fun insertSong(song : Song);

    @Query("SELECT * FROM Song ORDER BY id ASC")
    fun readAllData() : LiveData<List<Song>>;
}

