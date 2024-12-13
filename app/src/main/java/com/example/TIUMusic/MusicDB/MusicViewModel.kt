package com.example.TIUMusic.MusicDB

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.TIUMusic.SongData.MusicItem
import dagger.hilt.android.qualifiers.ApplicationContext

class MusicViewModel(@ApplicationContext context: Context) : ViewModel() {
    val readAllData : List<Song>;
    private val _repository : MusicRepository;

    init {
        val musicDao = MusicDatabase.getDatabase(context).musicDao();
        _repository = MusicRepository(musicDao);
        readAllData = _repository.readAllData;
    }

    fun getAlbums(context : Context) : List<Album> {
        return _repository.albums;
    }

}