package com.example.TIUMusic.MusicDB

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TIUMusic.SongData.MusicItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val SEARCH_FILTER_SONG = 1 shl 0;
const val SEARCH_FILTER_ALBUM = 1 shl 1;
const val SEARCH_FILTER_PLAYLIST = 1 shl 2;
const val SEARCH_FILTER_ALL = SEARCH_FILTER_SONG or SEARCH_FILTER_ALBUM or SEARCH_FILTER_PLAYLIST;

@HiltViewModel
class MusicViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {

    private var _allSongs = MutableStateFlow<List<Song>>(listOf());
    val allSongs = _allSongs.asStateFlow();

    private var _playlists = MutableStateFlow<List<GlobalPlaylist>>(listOf());
    val playlist = _playlists.asStateFlow();

    private var _albums = MutableStateFlow<List<Album>>(listOf());
    val albums = _albums.asStateFlow();

    private var _searchResult = MutableStateFlow<List<MusicItem>>(listOf());
    val searchResult = _searchResult.asStateFlow();

    var searching : Boolean = false;
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

    fun searchSave(match : String, filter : Int, context: Context) {
        if (searching)
            return;
        viewModelScope.launch {
            searching = true;
            delay(500);
            _searchResult.value = search(match, filter, context);
        }
    }

    fun searchFlow(match : String, filter : Int, context: Context) : Flow<List<MusicItem>> = flow {
        if (searching)
            return@flow;
        delay(500);
        emit(search(match, filter, context))
    }

    private suspend fun search(match : String, filter : Int, context: Context) : List<MusicItem> {
        val result = mutableListOf<MusicItem>();
        if ((filter and SEARCH_FILTER_SONG) != 0) {
            val songs = _repository.searchSongs(match, 6).map { it.toMusicItem(context) };
            result.addAll(songs);
        }
        if ((filter and SEARCH_FILTER_ALBUM) != 0) {
            val albums = _repository.searchAlbum(match, 6).map { it.toMusicItem(context) };
            result.addAll(albums);
            println(albums);
        }
        if ((filter and SEARCH_FILTER_PLAYLIST) != 0) {
            val playlists = _repository.searchPlaylist(match, 6).map { it.toMusicItem(context) };
            result.addAll(playlists)
        }
        searching = false;
        return result;
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
        if (ids.isEmpty()) {
            emit(listOf())
            return@flow;
        }
        val songs = _repository.getSongsByIds(ids);
        emit(songs.map{ it.toMusicItem(context) })
    }

    fun getSongsWithIds(ids : String, context: Context) : Flow<List<MusicItem>> = flow {
        if (ids.isEmpty()) {
            emit(listOf())
            return@flow;
        }
        val songs = _repository.getSongsByIds(PlaylistSongsIdsToIdList(ids));
        emit(songs.map { it.toMusicItem(context) })
    }

    fun getSongsOrderedWithIds(ids : String, context: Context) : Flow<List<MusicItem>> = flow {
        if (ids.isEmpty()) {
            emit(listOf())
            return@flow;
        }
        val idList = PlaylistSongsIdsToIdList(ids)
        val songs = _repository.getSongsByIds(idList);
        emit(
            idList.mapNotNull { id ->
                val found = songs.binarySearch { b ->
                    val bId = b.id;
                    if (id < bId) 1;
                    else if (id > bId) -1;
                    else 0;
                }
                if (found in 0 until songs.size && songs[found].id == id)
                    return@mapNotNull songs[found].toMusicItem(context);
                else
                    return@mapNotNull null;
            }
        )
    }

    fun getSongsOrderedWithIds(ids : List<Int>, context: Context) : Flow<List<MusicItem>> = flow {
        if (ids.isEmpty()) {
            emit(listOf())
            return@flow;
        }
        val songs = _repository.getSongsByIds(ids);
        emit(
            ids.mapNotNull { id ->
                val found = songs.binarySearch { b ->
                    val bId = b.id;
                    if (id < bId) 1;
                    else if (id > bId) -1;
                    else 0;
                }
                if (found in 0 until songs.size && songs[found].id == id)
                    return@mapNotNull songs[found].toMusicItem(context);
                else
                    return@mapNotNull null;
            }
        )
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