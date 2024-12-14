package com.example.TIUMusic.MusicDB

import android.content.Context
import android.provider.Settings.Global
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TIUMusic.SongData.MusicItem
import com.example.TIUMusic.SongData.MusicItemType
import com.example.TIUMusic.Utils.nameToRID
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MusicViewModel(@ApplicationContext context: Context) : ViewModel() {

    private var _allSongs = MutableStateFlow<List<Song>>(listOf());
    val allSongs = _allSongs.asStateFlow();

    private var _playlists = MutableStateFlow<List<GlobalPlaylist>>(listOf());
    val playlist = _playlists.asStateFlow();

    private var _albums = MutableStateFlow<List<Album>>(listOf());
    val albums = _albums.asStateFlow();

    private val _repository : MusicRepository;

    init {
        val musicDao = MusicDatabase.getDatabase(context).musicDao();
        _repository = MusicRepository(musicDao);
    }

    fun getAlbums() {
        viewModelScope.launch {
            _albums.value = _repository.getAllAlbums();
        }
    }

    fun getRandomAlbums(limit : Int = 5, context : Context) : Flow<List<MusicItem>> = flow {
        val albums = _repository.getRandomAlbums(limit);
        emit(albums.map { it.toMusicItem(context) })
    }

    fun getRandomPlaylist(limit : Int = 5, context : Context) : Flow<List<MusicItem>> = flow {
        val playlists = _repository.getRandomPlaylists(limit);
        emit(playlists.map { it.toMusicItem(context) })
    }

    fun getNewSongReleases(limit : Int = 5, context: Context) : Flow<List<MusicItem>> = flow {
        val songs = _repository.getNewSongReleases(limit);
        emit(songs.map { it.toMusicItem(context) })
    }

    fun getNewAlbumsReleases(limit : Int = 5, context: Context) : Flow<List<MusicItem>> = flow {
        val albums = _repository.getNewAlbumsReleases(limit);
        emit(albums.map { it.toMusicItem(context) })
    }

    fun getSongsInAlbum(albumId : Int, context : Context) : Flow<List<MusicItem>> = flow {
        val songs = _repository.getSongsByAlbumId(albumId);
        emit(songs.map { it.toMusicItem(context) })
    }

    fun getSongsWithIds(ids : List<Int>, context: Context) : Flow<List<MusicItem>> = flow {
        val songs = _repository.getSongsByIds(ids);
        emit(songs.map{ it.toMusicItem(context) })
    }

    fun getSongsWithIds(ids : String, context: Context) : Flow<List<MusicItem>> = flow {
        val songs = _repository.getSongsByIds(PlaylistSongsIdsToIdList(ids));
        emit(songs.map { it.toMusicItem(context) })
    }

    fun getRandomSongs(limit : Int = 10, context: Context) : Flow<List<MusicItem>> = flow {
        val songs = _repository.getRandomSongs(limit);
        emit(songs.map { it.toMusicItem(context) });
    }

}

fun PlaylistSongsIdsToIdList(ids : String) : List<Int> {
    var split = ids.filter { it in '0'..'9' || it == ',' }.split(',');
    return split.mapNotNull { it.toIntOrNull() };
}