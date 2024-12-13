package com.example.TIUMusic.MusicDB

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class MusicViewModel(@ApplicationContext context: Context) : ViewModel() {
    private val readAllData : LiveData<List<Song>>;
    private val repository : MusicRepository;

    init {
        val musicDao = MusicDatabase.getDatabase(context).musicDao();
        repository = MusicRepository(musicDao);
        readAllData = repository.readAllData;
    }
}