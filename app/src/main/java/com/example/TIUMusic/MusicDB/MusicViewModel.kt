package com.example.TIUMusic.MusicDB

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.TIUMusic.SongData.MusicItem
import dagger.hilt.android.qualifiers.ApplicationContext

class MusicViewModel(@ApplicationContext context: Context) : ViewModel() {
    val readAllData : List<Song>;
    val playlist : List<GlobalPlaylist>;
    private val _repository : MusicRepository;

    init {
        val musicDao = MusicDatabase.getDatabase(context).musicDao();
        _repository = MusicRepository(musicDao);
        readAllData = _repository.readAllData;
        playlist = _repository.playlist;

        playlist.firstOrNull()?.let {
            Log.d("MusicViewModel",
                it.songsIds
                    .filter { !(it == ' ' || it == '\n') }
                    .split(',')
                    .toString())
        };
    }

    fun getAlbums(context : Context) : List<Album> {
        return _repository.albums;
    }

    fun getSongsInAlbum(albumId : Int, context : Context) : List<MusicItem> {
        val songs = _repository.getSongsByAlbumId(albumId);
        return songs.map {
            it.toMusicItem(context)
        }
    }

    fun getSongsWithIds(ids : List<Int>, context: Context) : List<MusicItem> {
        val songs = _repository.getSongsByIds(ids);
        return songs.map {
            it.toMusicItem(context)
        }
    }

    fun getSongsWithIds(ids : String, context: Context) : List<MusicItem> {
        val songs = _repository.getSongsByIds(PlaylistSongsIdsToIdList(ids));
        return songs.map {
            it.toMusicItem(context)
        }
    }

    fun getRandomSongs(limit : Int = 10, context: Context) : MutableList<MusicItem> {
        val songs = _repository.getRandomSongs(limit);
        return songs.map {
            it.toMusicItem(context);
        }.toMutableList();
    }
}

fun PlaylistSongsIdsToIdList(ids : String) : List<Int> {
    var split = ids.filter { it in '0'..'9' || it == ',' }.split(',');
    return split.mapNotNull { it.toIntOrNull() };
}