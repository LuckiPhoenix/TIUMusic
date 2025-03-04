package com.example.TIUMusic.Libs.YoutubeLib

import android.content.Context
import android.util.Log
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.TIUMusic.Libs.YoutubeLib.YouTube.ytMusic
import com.example.TIUMusic.Libs.YoutubeLib.models.Album
import com.example.TIUMusic.Libs.YoutubeLib.models.Artist
import com.example.TIUMusic.Libs.YoutubeLib.models.ArtistItem
import com.example.TIUMusic.Libs.YoutubeLib.models.GridRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.MusicCarouselShelfRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.PlaylistItem
import com.example.TIUMusic.Libs.YoutubeLib.models.SectionListRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.SongItem
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.Chart
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeContent
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeItem
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.PlaylistBrowse
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.TrendingSong
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.TrendingVideo
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.parseSongArtists
import com.example.TIUMusic.Libs.YoutubeLib.models.Thumbnail
import com.example.TIUMusic.Libs.YoutubeLib.models.VideoItem
import com.example.TIUMusic.Libs.YoutubeLib.models.WatchEndpoint
import com.example.TIUMusic.Libs.YoutubeLib.models.YouTubeClient.Companion.WEB_REMIX
import com.example.TIUMusic.Libs.YoutubeLib.models.oddElements
import com.example.TIUMusic.Libs.YoutubeLib.models.response.SearchResponse
import com.example.TIUMusic.Libs.YoutubeLib.pages.ArtistPage
import com.example.TIUMusic.Libs.YoutubeLib.pages.ExplorePage
import com.example.TIUMusic.Libs.YoutubeLib.pages.MoodAndGenres
import com.example.TIUMusic.Libs.YoutubeLib.pages.RelatedPage
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.SongData.AlbumItem
import com.example.TIUMusic.SongData.MoodItem
import com.example.TIUMusic.SongData.MusicItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object YtmusicModule {
    @Provides
    fun provideYtmusic(): Ytmusic {
        return Ytmusic()
    }
}

@HiltViewModel
class YtmusicViewModel @Inject constructor(
    private val ytmusic: Ytmusic, // Inject Ytmusic class (nếu dùng Hilt hoặc tạo instance thủ công)
) : ViewModel() {

    //Searching Data
    private val _searchResults = MutableStateFlow<List<MusicItem>>(emptyList())
    val searchResults: StateFlow<List<MusicItem>> = _searchResults

    private val _searchSuggests = MutableStateFlow<List<String>>(emptyList())
    val searchSuggests: StateFlow<List<String>> = _searchSuggests

    private val _searchFilter = MutableStateFlow("Top results")
    val searchFiler: StateFlow<String> get() = _searchFilter

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    //Mood Data
    private val _moodList = MutableStateFlow<List<Pair<String, List<MoodItem>>>>(emptyList())
    val moodList : StateFlow<List<Pair<String, List<MoodItem>>>> = _moodList

    private val _moodfetch = MutableStateFlow<Pair<String, List<MusicItem>>>(Pair("", emptyList()))
    val moodfetch : StateFlow<Pair<String, List<MusicItem>>> = _moodfetch

    //Artist Data
    private val _artistResult = MutableStateFlow<ArtistPage?>(null)
    val artistResult = _artistResult

    //Home Data
    private var _homeItems = MutableStateFlow<List<HomeItem>>(listOf());
    val homeItems : StateFlow<List<HomeItem>> = _homeItems.asStateFlow();

    private var _chart = MutableStateFlow<Chart?>(null);
    val chart = _chart.asStateFlow();

    private var _newReleases = MutableStateFlow<List<HomeItem>>(listOf());
    val newReleases = _newReleases.asStateFlow();

    private var _listTrackItems = MutableStateFlow<UiState<List<MusicItem>>>(UiState.Initial)
    val listTrackItems : StateFlow<UiState<List<MusicItem>>> = _listTrackItems.asStateFlow();

    private var _albumPage = MutableStateFlow<UiState<AlbumItem>>(UiState.Initial)
    val albumPage : StateFlow<UiState<AlbumItem>> = _albumPage.asStateFlow()

    private var _homeContinuation = mutableIntStateOf(0);
    val homeContinuation = _homeContinuation.asIntState();

    private var _userPlaylists = MutableStateFlow<List<MusicItem>>(listOf());
    val userPlaylists : StateFlow<List<MusicItem>> = _userPlaylists.asStateFlow();

    var fetchingContinuation : Boolean = false;

    companion object {
        // Here because this class aint gonna use this
        suspend fun getRadio(videoId : String, originalTrack : MusicItem) : PlaylistBrowse? {
            val radioId = "RDAMVM$videoId";
            runCatching {
                YouTube
                    .next(endpoint = WatchEndpoint(playlistId = radioId))
                    .onSuccess { next ->
                        Log.w("Radio", "Title: ${next.title}")
                        val data: MutableList<MusicItem> = mutableListOf()
                        data.add(originalTrack);
                        data.addAll(next.items.map {
                            MusicItem(
                                videoId = it.id,
                                title = it.title,
                                artist = it.artists.firstOrNull()?.name ?: "",
                                imageUrl = it.thumbnails?.thumbnails?.lastOrNull()?.url ?: "",
                                type = 0,
                            )
                        })
                        var continuation = next.continuation
                        Log.w("Radio", "data: ${data.size}")
                        var count = 0
                        while (continuation != null && count < 3) {
                            YouTube
                                .next(
                                    endpoint = WatchEndpoint(playlistId = radioId),
                                    continuation = continuation,
                                ).onSuccess { nextContinue ->
                                    data.addAll(nextContinue.items.map {
                                        MusicItem(
                                            videoId = it.id,
                                            title = it.title,
                                            artist = it.artists.firstOrNull()?.name ?: "",
                                            imageUrl = it.thumbnails?.thumbnails?.firstOrNull()?.url ?: "",
                                            type = 0,
                                        )
                                    })
                                    continuation = nextContinue.continuation
                                    if (data.size >= 50) {
                                        count = 3
                                    }
                                    Log.w("Radio", "data: ${data.size}")
                                    count++
                                }.onFailure {
                                    count = 3
                                }
                        }
                        Log.w("Repository", "data: ${data.size}")
                        val playlistBrowse =
                            PlaylistBrowse(
                                id = radioId,
                                tracks = data,
                                originalTrack = originalTrack,
                            )
                        Log.w("Repository", "playlistBrowse: $playlistBrowse")
                        return playlistBrowse;
                    }.onFailure { exception ->
                        exception.printStackTrace()
                        return null;
                    }
            }
            return null;
        }
    }

    fun performSearch(query: String){
        var videoInfos: List<MusicItem>
        Log.d("viewModelTest", "RUN")
        viewModelScope.launch {
            _loading.value = true
            try {
                withContext(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
                    Log.e("viewModelTest", "Lỗi trong coroutine: ${throwable.message}")
                }) {
                    val client = WEB_REMIX
                    val response = ytmusic.search(client = client, query).body<SearchResponse>()

                    videoInfos = extractVideoInfo(response)

                    // Gán giá trị mới cho _searchResults
                    _searchResults.value = videoInfos
                }
            } catch (e: Exception) {
                // Xử lý ngoại lệ ở đây
                Log.d("viewModelTest", "Error occurred: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun suggestSearch(query: String){
        viewModelScope.launch {
            try{
                val suggestQuery = mutableListOf<String>()
                YouTube.getSuggestQuery(query)
                    .onSuccess { result ->
                        suggestQuery.clear()
                        result.take(4).forEach{ suggest ->
                            suggestQuery.add(
                                suggest
                            )
                        }
                        _searchSuggests.value = suggestQuery
                    }.onFailure {
                        suggestQuery.clear()
                        throw Exception(" Can't browse suggest query")
                    }
            }catch (e: Exception){
                // Xử lý ngoại lệ ở đây
                Log.d("viewModelTest", "Error suggest occurred : ${e.message}")
            }
        }
    }

    //Hàm cập nhật bộ lọc cho các kết quả tìm kiếm
    fun updateSearchFilter(newFilter: String){
        _searchFilter.value = newFilter
    }

    fun sortPlaylist(option : String, playlistId : String) {
        when (option) {
            "Title" -> {
                if (listTrackItems.value is UiState.Success)
                    (listTrackItems.value as UiState.Success<List<MusicItem>>).data.sortedBy { it.title };
            }
            "Artist" -> {
                if (listTrackItems.value is UiState.Success)
                    (listTrackItems.value as UiState.Success<List<MusicItem>>).data.sortedBy { it.artist };
            }
        }

    }

    // Hàm trích xuất thông tin video ID
    private fun extractVideoInfo(response: SearchResponse): List<MusicItem> {
        // Thông tin trả về
        val searchInfos = mutableListOf<MusicItem>()

        // Lấy tabs đầu tiên
        val listShelfRender = response.contents?.tabbedSearchResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents
            ?:throw Exception(" No renderer")

        // Duyệt
        for (renderer in listShelfRender.take(6)){
            if(renderer.musicCardShelfRenderer != null){
                val title = renderer.musicCardShelfRenderer.title.runs?.firstOrNull()?.text?:""
                val subtitle = renderer.musicCardShelfRenderer.subtitle.runs?.firstOrNull()?.text
                val endpoint = renderer.musicCardShelfRenderer.title.runs?.firstOrNull()?.navigationEndpoint
                    ?:throw Exception("No endpoint")
                val url = renderer.musicCardShelfRenderer.thumbnail.musicThumbnailRenderer?.getThumbnailUrl()?:""
                var item: MusicItem ?= null
                when(subtitle) {
                    "Album" -> {
                        item = MusicItem(
                            videoId = "",
                            title = title,
                            artist = "",
                            imageUrl = url,
                            type = 2,
                            browseId = endpoint.browseEndpoint?.browseId?:"",
                        )
                    }
                    "Artist" -> {
                        item = MusicItem(
                            videoId = "",
                            title = title,
                            artist = "",
                            imageUrl = url,
                            type = 3,
                            browseId = endpoint.browseEndpoint?.browseId?:"",
                        )
                    }
                }
                if(item != null){
                    searchInfos.add(item)
                }

                Log.d("viewModelTest", "Title: $title | Subtitle: $subtitle | Url: $url")
                if(endpoint.watchEndpoint != null){
                    Log.d("viewModelTest", "endpoint: ${endpoint.watchEndpoint.videoId}")
                }else{
                    Log.d("viewModelTest", "endpoint: ${endpoint.browseEndpoint?.browseId}")
                }

            }
            if(renderer.musicCardShelfRenderer == null && renderer.musicShelfRenderer != null){
                val title = renderer.musicShelfRenderer.title?.runs?.firstOrNull()?.text
                if (title == "Songs" && (searchFiler.value == title || searchFiler.value == "Top results")/*|| title == "Videos"*/){ //Tam bo vi may videos kha xam :v
                    val contents = renderer.musicShelfRenderer.contents
                        ?: throw Exception(" - No content in Renderer found")
                    for(content in contents) {
                        val item = content.musicResponsiveListItemRenderer?.flexColumns
                            ?: throw Exception(" - No musicResponsiveListItemFlexColumnRenderer found")

                        val songRender =
                            item[0].musicResponsiveListItemFlexColumnRenderer.text?.runs?.firstOrNull()
                                ?: throw Exception(" - No songRenderer found")
                        val artistRender = item[1].musicResponsiveListItemFlexColumnRenderer.text?.runs
                            ?: throw Exception(" - No artistRenderer found")

                        var i = artistRender.indexOfFirst { it.navigationEndpoint != null }
                        if (i == -1){
                            i = 0
                        }
                        val thumbnail = content.musicResponsiveListItemRenderer?.thumbnail?.
                        musicThumbnailRenderer?.thumbnail?.thumbnails?.lastOrNull();
                        var thumbnailURL = "";
                        val videoId = songRender.navigationEndpoint?.watchEndpoint?.videoId;
                        if (videoId != null)
                            thumbnailURL = getYoutubeHDThumbnail(videoId);
                        else
                            thumbnailURL = thumbnail?.url ?: "";
                        searchInfos.add(
                            MusicItem(
                                videoId = songRender.navigationEndpoint?.watchEndpoint?.videoId?:"",
                                title = songRender.text,
                                artist = artistRender[i].text,
                                imageUrl = thumbnailURL,
                                type = 0,
                            )
                        )
                    }
                }
                else if(title == "Albums" && (searchFiler.value == title || searchFiler.value == "Top results")){
                    val contents = renderer.musicShelfRenderer.contents
                        ?: throw Exception(" - No content in Renderer found")

                    for(content in contents) {
                        val item = content.musicResponsiveListItemRenderer?.flexColumns
                            ?: throw Exception(" - No musicResponsiveListItemFlexColumnRenderer found")
                        val albumId = content.musicResponsiveListItemRenderer.navigationEndpoint?.browseEndpoint?.browseId?:""
                        val albumRender =
                            item[0].musicResponsiveListItemFlexColumnRenderer.text?.runs?.firstOrNull()
                                ?: throw Exception(" - No songRenderer found")
                        val artistRender = item[1].musicResponsiveListItemFlexColumnRenderer.text?.runs
                            ?: throw Exception(" - No artistRenderer found")

                        var i = artistRender.indexOfFirst { it.navigationEndpoint != null }
                        if (i == -1){
                            i = 0
                        }
                        val thumbnailURL = content.musicResponsiveListItemRenderer.thumbnail?.
                        musicThumbnailRenderer?.getThumbnailUrl()?:""

                        searchInfos.add(
                            MusicItem(
                                videoId = "",
                                title = albumRender.text,
                                artist = artistRender[i].text,
                                imageUrl = thumbnailURL,
                                type = 2,
                                browseId = albumId,
                            )
                        )
                    }
                }
                else if((title == "Featured playlists" || title == "Community playlists") && (searchFiler.value == "Playlists"|| searchFiler.value == "Top results")){
                    val contents = renderer.musicShelfRenderer.contents
                        ?: throw Exception(" - No content in Renderer found")

                    for(content in contents) {
                        val item = content.musicResponsiveListItemRenderer?.flexColumns
                            ?: throw Exception(" - No musicResponsiveListItemFlexColumnRenderer found")
                        val albumId = content.musicResponsiveListItemRenderer.navigationEndpoint?.browseEndpoint?.browseId?:""
                        val albumRender =
                            item[0].musicResponsiveListItemFlexColumnRenderer.text?.runs?.firstOrNull()
                                ?: throw Exception(" - No songRenderer found")
                        val artistRender = item[1].musicResponsiveListItemFlexColumnRenderer.text?.runs
                            ?: throw Exception(" - No artistRenderer found")

                        var i = artistRender.indexOfFirst { it.navigationEndpoint != null }
                        if (i == -1){
                            i = 0
                        }
                        val thumbnailURL = content.musicResponsiveListItemRenderer.thumbnail?.
                        musicThumbnailRenderer?.getThumbnailUrl()?:""

                        searchInfos.add(
                            MusicItem(
                                videoId = "",
                                title = albumRender.text,
                                artist = artistRender[i].text,
                                imageUrl = thumbnailURL,
                                type = 1,
                                playlistId = albumId.removePrefix("VL"),
                            )
                        )
                    }
                }
                else if(title == "Artists" && (searchFiler.value == title || searchFiler.value == "Top results")){
                    val contents = renderer.musicShelfRenderer.contents
                        ?: throw Exception(" - No content in Renderer found")

                    for(content in contents) {
                        val item = content.musicResponsiveListItemRenderer?.flexColumns
                            ?: throw Exception(" - No musicResponsiveListItemFlexColumnRenderer found")
                        val artistId = content.musicResponsiveListItemRenderer.navigationEndpoint?.browseEndpoint?.browseId?:""
                        val artistRender =
                            item[0].musicResponsiveListItemFlexColumnRenderer.text?.runs?.firstOrNull()
                                ?: throw Exception(" - No songRenderer found")
                        val followersRender = item[1].musicResponsiveListItemFlexColumnRenderer.text?.runs
                            ?: throw Exception(" - No artistRenderer found")

                        val thumbnailURL = content.musicResponsiveListItemRenderer.thumbnail?.
                        musicThumbnailRenderer?.getThumbnailUrl()?:""

                        searchInfos.add(
                            MusicItem(
                                videoId = "",
                                title = artistRender.text,
                                artist = followersRender[2].text,
                                imageUrl = thumbnailURL,
                                type = 3,
                                browseId = artistId,
                            )
                        )
                    }
                }
            }
        }
        return searchInfos
    }

    fun resetHome() {
        if (fetchingContinuation)
            return;
        _homeItems.value = listOf();
    }

    fun getHomeContinuation(context: Context, first : Boolean = false) {
        if (first && homeItems.value.isNotEmpty())
            return;
        if (fetchingContinuation || homeContinuation.intValue > 15)
            return;
        fetchingContinuation = true;
        viewModelScope.launch {
            val items = getHomeScreen(context)
            if (items.isNotEmpty()) {
                val newHome = mutableListOf<HomeItem>();
                newHome.addAll(_homeItems.value);
                if (_homeItems.value.isEmpty()) {
                    newHome.addAll(items);
                }
                else {
                    for (item in items) {
                        if (newHome.find { (it.title == item.title) } == null)
                            newHome.add(item);
                    }
                }
                _homeItems.value = (newHome);
            }
            fetchingContinuation = false;
        }
        _homeContinuation.intValue++;
    }

    private suspend fun getHomeScreen(context: Context) : List<HomeItem> {
        runCatching {
            YouTube.customQuery(browseId = "FEmusic_home")
                .onSuccess { result ->
                    var continueParam =
                        result.contents?.singleColumnBrowseResultsRenderer?.tabs?.get(0)?.tabRenderer?.content?.sectionListRenderer?.continuations?.get(
                            0
                        )?.nextContinuationData?.continuation
                    val data =
                        result.contents?.singleColumnBrowseResultsRenderer?.tabs?.get(0)?.tabRenderer?.content?.sectionListRenderer?.contents;
                    val list: MutableList<HomeItem> = mutableListOf()
                    list.addAll(parseHomeScreen(data, context))
                    var count = 0
                    while (count < _homeContinuation.intValue && continueParam != null) {
                        YouTube.customQuery(browseId = "", continuation = continueParam)
                            .onSuccess { response ->
                                continueParam =
                                    response.continuationContents
                                        ?.sectionListContinuation
                                        ?.continuations
                                        ?.get(
                                            0,
                                        )?.nextContinuationData
                                        ?.continuation
                                Log.d("Repository", "continueParam: $continueParam")
                                val dataContinue =
                                    response.continuationContents?.sectionListContinuation?.contents
                                list.addAll(parseHomeScreen(dataContinue, context))
                                count++
                                Log.d("Repository", "count: $count")
                            }.onFailure {
                                Log.e("Repository", "Error: ${it.message}")
                                count++
                            }
                    }
                    return list;
                }
                .onFailure { error ->
                    Log.e("YtmusicViewModel", error.message.toString());
                    return listOf();
                }
        }
        return listOf();
    }

    private fun parseHomeScreen(
        data : List<SectionListRenderer.Content>?,
        context: Context
    ) : List<HomeItem> {
        val list = mutableListOf<HomeItem>();
        if (data != null) {
            for (row in data) {
                if (row.musicDescriptionShelfRenderer != null) {
                    val results = row.musicDescriptionShelfRenderer
                    val title = results.header?.runs?.get(0)?.text ?: ""
                    val content = results.description.runs?.get(0)?.text ?: ""
                    if (title.isNotEmpty()) {
                        list.add(
                            HomeItem(
                                contents =
                                listOf(
                                    HomeContent(
                                        album = null,
                                        artists = listOf(),
                                        description = content,
                                        isExplicit = null,
                                        playlistId = null,
                                        browseId = null,
                                        thumbnails = listOf(),
                                        title = content,
                                        videoId = null,
                                        views = null,
                                    ),
                                ),
                                title = title,
                            ),
                        )
                    }
                }
                else {
                    val results = row.musicCarouselShelfRenderer
                    Log.w("parse_mixed_content", results.toString())

                    val title =
                        results?.
                            header?.
                            musicCarouselShelfBasicHeaderRenderer?.
                            title?.
                            runs?.
                            get(0)?.
                            text ?: ""
                    Log.w("parse_mixed_content", title)
                    val subtitle =
                        results
                            ?.header
                            ?.musicCarouselShelfBasicHeaderRenderer
                            ?.strapline
                            ?.runs
                            ?.firstOrNull()
                            ?.text
                    val thumbnail =
                        results
                            ?.header
                            ?.musicCarouselShelfBasicHeaderRenderer
                            ?.thumbnail
                            ?.musicThumbnailRenderer
                            ?.thumbnail
                            ?.thumbnails
                    val artistChannelId =
                        results
                            ?.header
                            ?.musicCarouselShelfBasicHeaderRenderer
                            ?.title
                            ?.runs
                            ?.firstOrNull()
                            ?.navigationEndpoint
                            ?.browseEndpoint
                            ?.browseId

                    val listContent = mutableListOf<HomeContent?>()
                    val contentList = results?.contents
                    Log.w("parse_mixed_content", results?.contents?.size.toString())
                    if (!contentList.isNullOrEmpty()) {
                        for (result in contentList) {
                            val musicTwoRowItemRenderer = result.musicTwoRowItemRenderer
                            if (musicTwoRowItemRenderer != null) {
                                if (musicTwoRowItemRenderer.isSong) {
                                    val ytItem =
                                        RelatedPage.fromMusicTwoRowItemRenderer(
                                            musicTwoRowItemRenderer
                                        ) as SongItem?
                                    val artists =
                                        ytItem
                                            ?.artists
                                            ?.map { Artist(name = it.name,id = it.id,) }?.toMutableList()
                                    Log.w("Song", ytItem.toString())
                                    if (ytItem != null) {
                                        listContent.add(
                                            HomeContent(
                                                album =
                                                ytItem.album?.let {
                                                    Album(name = it.name,id = it.id)
                                                },
                                                artists = artists,
                                                description = null,
                                                isExplicit = ytItem.explicit,
                                                playlistId = null,
                                                browseId = null,
                                                thumbnails =
                                                musicTwoRowItemRenderer.thumbnailRenderer.musicThumbnailRenderer
                                                    ?.thumbnail
                                                    ?.thumbnails
                                                    ?: listOf(),
                                                title = ytItem.title,
                                                videoId = ytItem.id,
                                                views = null,
                                                durationSeconds = ytItem.duration,
                                                radio = null,
                                            ),
                                        )
                                    }
                                }
                                else if (musicTwoRowItemRenderer.isVideo) {
                                    val ytItem =
                                        ArtistPage.fromMusicTwoRowItemRenderer(
                                            musicTwoRowItemRenderer
                                        ) as VideoItem?
                                    Log.w("Video", ytItem.toString())
                                    val artists =
                                        ytItem
                                            ?.artists
                                            ?.map {
                                                Artist(
                                                    name = it.name,
                                                    id = it.id,
                                                )
                                            }?.toMutableList()
//                                    if (artists?.lastOrNull()?.id == null &&
//                                        artists?.lastOrNull()?.name?.contains(
//                                            Regex("\\d"),
//                                        ) == true
//                                    ) {
//                                        runCatching { artists.removeAt(artists.lastIndex) }
//                                            .onSuccess {
//                                                Log.i(
//                                                    "parse_mixed_content",
//                                                    "Removed last artist"
//                                                )
//                                            }.onFailure {
//                                                Log.e(
//                                                    "parse_mixed_content",
//                                                    "Failed to remove last artist"
//                                                )
//                                                it.printStackTrace()
//                                            }
//                                    }
                                    if (ytItem != null) {
                                        listContent.add(
                                            HomeContent(
                                                album =
                                                ytItem.album?.let {
                                                    Album(
                                                        name = it.name,
                                                        id = it.id,
                                                    )
                                                },
                                                artists = artists,
                                                description = null,
                                                isExplicit = ytItem.explicit,
                                                playlistId = null,
                                                browseId = null,
                                                thumbnails =
                                                musicTwoRowItemRenderer.thumbnailRenderer.musicThumbnailRenderer
                                                    ?.thumbnail
                                                    ?.thumbnails
                                                    ?: listOf(),
                                                title = ytItem.title,
                                                videoId = ytItem.id,
                                                views = ytItem.view,
                                                durationSeconds = ytItem.duration,
                                                radio = null,
                                            ),
                                        )
                                    }
                                }
                                else if (musicTwoRowItemRenderer.isArtist) {
                                    val ytItem =
                                        RelatedPage.fromMusicTwoRowItemRenderer(
                                            musicTwoRowItemRenderer
                                        ) as ArtistItem?
                                    Log.w("Artists", ytItem.toString())
                                    if (ytItem != null) {
                                        listContent.add(
                                            HomeContent(
                                                album = null,
                                                artists = listOf(),
                                                description = null,
                                                isExplicit = null,
                                                playlistId = null,
                                                browseId = ytItem.id,
                                                thumbnails =
                                                musicTwoRowItemRenderer.thumbnailRenderer.musicThumbnailRenderer
                                                    ?.thumbnail
                                                    ?.thumbnails
                                                    ?: listOf(),
                                                title = ytItem.title,
                                                videoId = null,
                                                views = null,
                                                radio = null,
                                            ),
                                        )
                                    }
                                }
                                else if (musicTwoRowItemRenderer.isAlbum) {
                                    listContent.add(
                                        HomeContent(
                                            album =
                                            Album(
                                                id =
                                                musicTwoRowItemRenderer.navigationEndpoint.browseEndpoint?.browseId
                                                    ?: "",
                                                name = title,
                                            ),
                                            artists = listOfNotNull(
                                                musicTwoRowItemRenderer.subtitle?.runs?.lastOrNull()?.let {
                                                    Artist(
                                                        name = it.text ?: "",
                                                        id = it.navigationEndpoint?.browseEndpoint?.browseId ?: ""
                                                    )
                                                }
                                            ),
                                            description = null,
                                            isExplicit = false,
                                            playlistId = null,
                                            browseId = musicTwoRowItemRenderer.navigationEndpoint.browseEndpoint?.browseId,
                                            thumbnails =
                                            musicTwoRowItemRenderer.thumbnailRenderer.musicThumbnailRenderer
                                                ?.thumbnail
                                                ?.thumbnails
                                                ?: listOf(),
                                            title =
                                            musicTwoRowItemRenderer.title.runs
                                                ?.get(0)
                                                ?.text
                                                ?: "",
                                            videoId = "",
                                            views = "",
                                        ),
                                    )
                                }
                                else if (musicTwoRowItemRenderer.isPlaylist) {
                                    val subtitle1 = musicTwoRowItemRenderer.subtitle
                                    var description = ""
                                    if (subtitle1 != null) {
                                        if (subtitle1.runs != null) {
                                            for (run in subtitle1.runs!!) {
                                                description += run.text
                                            }
                                        }
                                    }
                                    if (musicTwoRowItemRenderer.navigationEndpoint.browseEndpoint?.browseId?.startsWith(
                                            "MPRE",
                                        ) == true
                                    ) {
                                        listContent.add(
                                            HomeContent(
                                                album =
                                                Album(
                                                    id =
                                                    musicTwoRowItemRenderer.navigationEndpoint.browseEndpoint?.browseId
                                                        ?: "",
                                                    name = title,
                                                ),
                                                artists = listOf(),
                                                description = null,
                                                isExplicit = false,
                                                playlistId = null,
                                                browseId = musicTwoRowItemRenderer.navigationEndpoint.browseEndpoint?.browseId,
                                                thumbnails =
                                                musicTwoRowItemRenderer.thumbnailRenderer.musicThumbnailRenderer
                                                    ?.thumbnail
                                                    ?.thumbnails
                                                    ?: listOf(),
                                                title =
                                                musicTwoRowItemRenderer.title.runs
                                                    ?.get(
                                                        0,
                                                    )?.text ?: "",
                                                videoId = "",
                                                views = "",
                                            ),
                                        )
                                    }
                                    else {
                                        val ytItem1 =
                                            RelatedPage.fromMusicTwoRowItemRenderer(
                                                musicTwoRowItemRenderer,
                                            ) as PlaylistItem?
                                        ytItem1?.let { ytItem ->
                                            listContent.add(
                                                HomeContent(
                                                    album = null,
                                                    artists =
                                                    listOf(
                                                        Artist(
                                                            id = ytItem.author?.id ?: "",
                                                            name = ytItem.author?.name ?: "",
                                                        ),
                                                    ),
                                                    description = description,
                                                    isExplicit = ytItem.explicit,
                                                    playlistId = ytItem.id,
                                                    browseId = ytItem.id,
                                                    thumbnails =
                                                    musicTwoRowItemRenderer.thumbnailRenderer.musicThumbnailRenderer
                                                        ?.thumbnail
                                                        ?.thumbnails
                                                        ?: listOf(),
                                                    title = ytItem.title,
                                                    videoId = null,
                                                    views = null,
                                                    radio = null,
                                                ),
                                            )
                                        }
                                    }
                                }
                                else {
                                    continue
                                }
                            }


                            else if (result.musicResponsiveListItemRenderer != null) {
                                Log.w(
                                    "parse Song flat",
                                    result.musicResponsiveListItemRenderer.toString(),
                                )
                                val ytItem =
                                    RelatedPage.fromMusicResponsiveListItemRenderer(result.musicResponsiveListItemRenderer!!)
                                if (ytItem != null) {
                                    val content =
                                        HomeContent(
                                            album = ytItem.album?.let {
                                                Album(
                                                    name = it.name,
                                                    id = it.id
                                                )
                                            },
                                            artists =
                                            parseSongArtists(
                                                result.musicResponsiveListItemRenderer!!,
                                                1,
                                                context,
                                            ) ?: listOf(),
                                            description = null,
                                            isExplicit = false,
                                            playlistId = null,
                                            browseId = null,
                                            thumbnails =
                                            result.musicResponsiveListItemRenderer!!
                                                .thumbnail
                                                ?.musicThumbnailRenderer
                                                ?.thumbnail
                                                ?.thumbnails
                                                ?: listOf(),
                                            title = ytItem.title,
                                            videoId = ytItem.id,
                                            views = "",
                                            radio = null,
                                        )
                                    listContent.add(content)
                                }
                            }
                            else {
                                break
                            }
                        }
                    }
                    if (title.isNotEmpty()) {
                        list.add(
                            HomeItem(
                                contents = listContent,
                                title = title,
                                subtitle = subtitle,
                                thumbnail = thumbnail,
                                channelId = if (artistChannelId?.contains("UC") == true) artistChannelId else null,
                            ),
                        )
                    }
                    if (list.lastOrNull() != null) {
                        if (list.last().contents.isEmpty())
                            list.removeAt(list.size - 1);
                    }
                    Log.w("parse_mixed_content", list.toString())
                }
            }
        }
        return list;
    }

    fun resetNewScreen() {
        _chart.value = null;
        _newReleases.value = listOf();
    }

    fun getNewScreen(countryCode: String = "US", context : Context, first : Boolean = false) {
        if (first && (chart.value != null))
            return;
        getChart(countryCode);
        getNewReleases(context);
    }

    fun getChart(countryCode: String = "US") {
        viewModelScope.launch {
            _chart.value = getChartData(countryCode);
        }
    }

    private suspend fun getChartData(countryCode : String) : Chart? {
        runCatching {
            YouTube
                .customQuery("FEmusic_charts", country = countryCode)
                .onSuccess { result ->
                    val data =
                        result.contents
                            ?.singleColumnBrowseResultsRenderer
                            ?.tabs
                            ?.get(
                                0,
                            )?.tabRenderer
                            ?.content
                            ?.sectionListRenderer
                    val chart = parseChart(data)
                    if (chart != null) {
                        return chart;
                    } else {
                        Log.e("YtmusicViewModel", "Error parsing chart");
                        return null;
                    }
                }.onFailure { error ->
                    Log.e("YtmusicViewModel", error.message.toString());
                }
        }
        return null;
    }

    private fun parseChart(data: SectionListRenderer?) : Chart? {
        if (data?.contents != null) {
            val trendingVideos = mutableListOf<TrendingVideo>();
            val trendingSongs = mutableListOf<TrendingSong>()
            var videoPlaylistId = "";
            for (section in data.contents!!) {
                if (section.musicCarouselShelfRenderer != null) {
                    val musicCarouselShelfRenderer = section.musicCarouselShelfRenderer
                    val pageType =
                        musicCarouselShelfRenderer?.header?.musicCarouselShelfBasicHeaderRenderer?.title?.runs?.get(0)?.
                            navigationEndpoint?.browseEndpoint?.browseEndpointContextSupportedConfigs?.browseEndpointContextMusicConfig?.pageType

                    if (pageType == "MUSIC_PAGE_TYPE_PLAYLIST" && musicCarouselShelfRenderer.numItemsPerColumn == null) {
                        videoPlaylistId =
                            musicCarouselShelfRenderer.header?.musicCarouselShelfBasicHeaderRenderer?.
                                title?.runs?.get(0)?.navigationEndpoint?.browseEndpoint?.browseId ?: ""
                        val contents = musicCarouselShelfRenderer.contents
                        trendingVideos.addAll(parseSongChart(contents))
                    }
                    else if (pageType == "MUSIC_PAGE_TYPE_PLAYLIST" && musicCarouselShelfRenderer.numItemsPerColumn == "4") {
                        val contents = musicCarouselShelfRenderer.contents
                        contents.forEachIndexed { index, content ->
                            val musicResponsiveListItemRenderer = content.musicResponsiveListItemRenderer

                            if (musicResponsiveListItemRenderer != null) {
                                val thumb = musicResponsiveListItemRenderer.thumbnail?.musicThumbnailRenderer?.thumbnail?.thumbnails
//                                val firstThumb = thumb?.firstOrNull()
//                                if (firstThumb != null && (firstThumb.width == firstThumb.height && firstThumb.width != null)) {
                                    val song =
                                        TrendingSong(
                                            album = musicResponsiveListItemRenderer.flexColumns.getOrNull(2)?.
                                                musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()
                                                ?.let {
                                                    Album(
                                                        name = it.text,
                                                        id = it.navigationEndpoint?.browseEndpoint?.browseId ?: ""
                                                    )
                                                },
                                            artists = musicResponsiveListItemRenderer.flexColumns.getOrNull(1)?.
                                                musicResponsiveListItemFlexColumnRenderer?.text?.runs?.oddElements()
                                                ?.map {
                                                    Artist(
                                                        name = it.text,
                                                        id = it.navigationEndpoint?.browseEndpoint?.browseId
                                                    )
                                                },
                                            thumbnail = thumb?.lastOrNull()!!.url ?: "",
                                            title = musicResponsiveListItemRenderer.flexColumns.firstOrNull()
                                                ?.musicResponsiveListItemFlexColumnRenderer?.text?.runs?.firstOrNull()
                                                ?.text ?: "",
                                            videoId = musicResponsiveListItemRenderer.playlistItemData?.videoId ?: "",
                                        )
                                    trendingSongs.add(song)
//                                }
                            }
                        }
                    }
                }
            }
            return Chart(
                videoPlaylist = Chart.TrendingVideoPlaylist(
                    playlistId = videoPlaylistId,
                    videos = trendingVideos
                ),
                songs = trendingSongs,
            )
        }
        return null;
    }

    private fun parseSongChart(contents : List<MusicCarouselShelfRenderer.Content>) : List<TrendingVideo> {
        val listVideoItem: ArrayList<TrendingVideo> = arrayListOf()
        for (content in contents) {
            val title = content.musicTwoRowItemRenderer?.title?.runs?.get(0)?.text
            val runs = content.musicTwoRowItemRenderer?.subtitle?.runs
            var view = ""
            val artists = mutableListOf<Artist>()
            val albums = mutableListOf<Album>()
            if (runs != null) {
                for (i in runs.indices) {
                    if (i.rem(2) == 0) {
                        if (i == runs.size - 1) {
                            view += runs[i].text
                        } else {
                            val name = runs[i].text
                            val id = runs[i].navigationEndpoint?.browseEndpoint?.browseId
                            if (id != null) {
                                if (id.startsWith("MPRE")) {
                                    albums.add(Album(id = id, name = name))
                                } else {
                                    artists.add(Artist(name = name, id = id))
                                }
                            }
                        }
                    }
                }
            }
            val thumbnails =
                content.musicTwoRowItemRenderer?.thumbnailRenderer?.musicThumbnailRenderer?.thumbnail?.thumbnails
            val videoId = content.musicTwoRowItemRenderer?.navigationEndpoint?.watchEndpoint?.videoId
            listVideoItem.add(
                TrendingVideo(
                    artists = artists,
                    playlistId = "",
                    thumbnail = thumbnails?.lastOrNull()?.url ?: "",
                    title = title ?: "",
                    videoId = videoId ?: "",
                    views = view
                )
            )
        }
        return listVideoItem
    }

    fun getNewReleases(context: Context) {
        viewModelScope.launch {
            _newReleases.value = newRelease(context);
        }
    }

    private suspend fun newRelease(context: Context) : List<HomeItem> {
        YouTube.newRelease().onSuccess { result ->
            return parseNewRelease(result, context);
        }.onFailure {
            Log.e("YtmusicViewModel", it.message.toString());
        }
        return listOf();
    }

    private fun parseNewRelease(explore: ExplorePage, context: Context) : List<HomeItem> {
        val result = mutableListOf<HomeItem>()
        result.add(
            HomeItem(
                title = "New Release",
                contents =
                explore.released.map {
                    HomeContent(
                        album = null,
                        artists =
                        listOf(
                            Artist(
                                id = it.author?.id ?: "",
                                name = it.author?.name ?: "",
                            ),
                        ),
                        description = it.author?.name ?: "YouTube Music",
                        isExplicit = it.explicit,
                        playlistId = it.id,
                        browseId = it.id,
                        thumbnails = listOf(Thumbnail(it.thumbnail, 0, 0)),
                        title = it.title,
                        videoId = null,
                        views = null,
                        radio = null,
                    )
                },
            ),
        )
        result.add(
            HomeItem(
                title = "Albums",
                contents =
                explore.albums.map { albumItem ->
                    val artists = albumItem.artists?.map { Artist(name = it.name, id = it.id) }?.toMutableList()
                    HomeContent(
                        album = null,
                        artists = artists,
                        description = null,
                        isExplicit = albumItem.explicit,
                        playlistId = albumItem.playlistId,
                        browseId = albumItem.browseId,
                        thumbnails = listOf(Thumbnail(albumItem.thumbnail, 0, 0)),
                        title = albumItem.title,
                        videoId = null,
                        views = null,
                        durationSeconds = null,
                        radio = null,
                    )
                },
            ),
        )
        result.add(
            HomeItem(
                title = "Music Videos",
                contents =
                explore.musicVideo.map { videoItem ->
                    val artists = videoItem.artists
                        .map { Artist(name = it.name, id = it.id) }.toMutableList()
                    HomeContent(
                        album = videoItem.album?.let { Album(name = it.name,id = it.id) },
                        artists = artists,
                        description = null,
                        isExplicit = videoItem.explicit,
                        playlistId = null,
                        browseId = null,
                        thumbnails = listOf(Thumbnail(videoItem.thumbnail, 0, 0)),
                        title = videoItem.title,
                        videoId = videoItem.id,
                        views = videoItem.view,
                        durationSeconds = videoItem.duration,
                        radio = null,
                    )
                },
            ),
        )
        return result
    }

    fun SongListSample(playlistId: String){
        if(!isPlaylistRandomUUID(playlistId)){
            viewModelScope.launch {
                runCatching {
                    _listTrackItems.value = UiState.Loading
                    YouTube.getPlaylistFullTracks(playlistId)
                }.onSuccess {result ->
                    result.onSuccess { tracks ->
                        val musicItems = tracks.map { songItem ->
                            val thumbnail = songItem.thumbnails?.thumbnails?.lastOrNull();
                            var thumbnailUrl = "";
                            if (thumbnail != null && songItem.id.isNotEmpty()) {
                                thumbnailUrl = getYoutubeHDThumbnail(songItem.id);
                            }
                            MusicItem(
                                videoId = songItem.id,
                                title = songItem.title,
                                artist = songItem.artists.lastOrNull()?.name ?: "Unknown Artist",
                                imageUrl = thumbnailUrl,
                                type = 0
                            )
                        }
                        _listTrackItems.value = UiState.Success(musicItems)
                    }.onFailure { error ->
                        _listTrackItems.value = UiState.Error(error.message ?: "Unknown error")
                    }
                }.onFailure { exception ->
                    _listTrackItems.value = UiState.Error(exception.message ?: "Network error")
                }
            }
        }
        else{
            _listTrackItems.value = UiState.Loading
            _listTrackItems.value = UiState.Success(emptyList())
        }
    }

    fun fetchAlbumSongs(albumId: String){
        viewModelScope.launch {
            runCatching {
                _albumPage.value = UiState.Loading
                YouTube.album(albumId)
            }.onSuccess { result ->
                result.onSuccess { albumPages ->
                    val title = albumPages.album.title
                    val description = albumPages.description
                    var artists = ""
                    for (artist in albumPages.album.artists!!){
                        if(artists == ""){
                            artists += artist.name
                        }
                        else{
                            artists += " | " + artist.name
                        }
                    }
                    val year = albumPages.album.year
                    val image = albumPages.album.thumbnail
                    val duration = albumPages.duration
                    val songs = albumPages.songs.map { songItem ->
                        val thumbnail = songItem.thumbnails?.thumbnails?.lastOrNull();
                        var thumbnailUrl = "";
                        if (thumbnail != null && songItem.id.isNotEmpty()) {
                            thumbnailUrl = getYoutubeHDThumbnail(songItem.id);
                        }
                        MusicItem(
                            videoId = songItem.id,
                            title = songItem.title,
                            artist = songItem.artists.lastOrNull()?.name ?: "Unknown Artist",
                            imageUrl = thumbnailUrl,
                            type = 0
                        )
                    }
                    _albumPage.value = UiState.Success(
                        AlbumItem(
                            title = title,
                            description = description,
                            artist = artists,
                            year = year,
                            imageUrl = image,
                            duration = duration,
                            songs = songs)
                    )
                }.onFailure {error ->
                    _albumPage.value = UiState.Error(error.message ?: "Unknown error")
                }
            }.onFailure {error ->
                _albumPage.value = UiState.Error(error.message ?: "Network error")
            }
        }
    }

    fun fetchMoodAndGenres(){
        viewModelScope.launch{
            runCatching {
                YouTube.moodAndGenres()
            }.onSuccess { result ->
                result.onSuccess { genres ->
                    val moodsL = mutableListOf<Pair<String, List<MoodItem>>>()
                    for(list in genres){
                        val title = list.title
                        val moods = mutableListOf<MoodItem>()
                        for(item in list.items){
                            moods.add(
                                MoodItem(
                                    title = item.title,
                                    color = item.stripeColor.toInt(),
                                    params = item.endpoint.params?:""
                                )
                            )
                        }
                        moodsL.add(Pair(title, moods))
                    }
                    _moodList.value = moodsL
                }
            }
        }
    }

    fun fetchMoodItem(browseId: String = "FEmusic_moods_and_genres_category", params: String){
        viewModelScope.launch {
            runCatching {
                _moodfetch.value = Pair("", emptyList())
                YouTube.browse(browseId, params)
            }.onSuccess { result ->
                result.onSuccess {
                    val newlist = mutableListOf<MusicItem>()
                    val title = it.title?:""
                    var suggestList = it.items.firstOrNull()
                        ?: throw Exception("")
                    if(suggestList.items.isEmpty()){
                        suggestList = it.items[2]
                    }
                    for (playlist in suggestList.items){
                        Log.d("Mood&Genres", "Title: ${playlist.title} | Url: ${playlist.thumbnail} | ID: ${playlist.id}")
                        newlist.add(
                            MusicItem(
                                videoId = "",
                                title = playlist.title,
                                artist = "",
                                imageUrl = playlist.thumbnail,
                                type = 1,
                                playlistId = playlist.id
                            )
                        )
                    }
                    _moodfetch.value = Pair(title, newlist)
                }
            }
        }
    }

    //Library Playlists
    fun isPlaylistRandomUUID(id: String): Boolean {
        val uuidRegex = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$")
        return uuidRegex.matches(id)
    }

    fun getUserPlaylists(first : Boolean = false) {
        if (first && userPlaylists.value.isNotEmpty())
            return;
        viewModelScope.launch {
            _userPlaylists.value = parseUserPlaylists() ?: listOf();
        }
    }

    private suspend fun parseUserPlaylists() : List<MusicItem>? {
        YouTube
            .getLibraryPlaylists()
            .onSuccess { data ->
                val input =
                    data.contents
                        ?.singleColumnBrowseResultsRenderer
                        ?.tabs
                        ?.get(
                            0,
                        )?.tabRenderer
                        ?.content
                        ?.sectionListRenderer
                        ?.contents
                        ?.get(
                            0,
                        )?.gridRenderer
                        ?.items
                if (input != null) {
                    val list = parseLibraryPlaylist(input)
                    return list;
                }
            }.onFailure { e ->
                Log.e("Library", "Error: ${e.message}")
                e.printStackTrace()
            }
        return null;
    }

    private fun parseLibraryPlaylist(items : List<GridRenderer.Item>) : List<MusicItem> {
        val list : MutableList<MusicItem> = mutableListOf()
        if (items.isNotEmpty()) {
            for (i in items.indices) {
                items[i].musicTwoRowItemRenderer?.let {
                    if (it.navigationEndpoint.browseEndpoint?.browseId != "VLSE" && it.navigationEndpoint.browseEndpoint?.browseId != null) {
                        val playlistId = it.navigationEndpoint.browseEndpoint.browseId.removeRange(0, 2) ?: "";
                        list.add(MusicItem(
                            videoId = "",
                            title = it.title.runs?.get(0)?.text ?: "",
                            artist = it.subtitle?.runs?.get(0)?.text ?: "",
                            playlistId = playlistId,
                            imageUrl = it.thumbnailRenderer.musicThumbnailRenderer?.thumbnail?.thumbnails?.lastOrNull()?.url
                                ?: "",
                            type = 1
                        ))
                        Log.d("Playlist", playlistId);
                    }
                }
            }
        }
        return list
    }

    //Artist
    fun fetchArtist(artistId: String){
        viewModelScope.launch {
            runCatching {
                YouTube.artist(artistId)
            }.onSuccess {
                it.onSuccess { artistPage ->
                    _artistResult.value = artistPage
                }
            }
        }
    }

    sealed class UiState<out T> {
        object Initial : UiState<Nothing>()
        object Loading : UiState<Nothing>()
        data class Success<T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()
    }
}