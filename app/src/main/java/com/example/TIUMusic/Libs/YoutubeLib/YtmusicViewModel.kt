package com.example.TIUMusic.Libs.YoutubeLib

import android.content.Context
import android.util.Log
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TIUMusic.Libs.YoutubeLib.models.Album
import com.example.TIUMusic.Libs.YoutubeLib.models.Artist
import com.example.TIUMusic.Libs.YoutubeLib.models.ArtistItem
import com.example.TIUMusic.Libs.YoutubeLib.models.PlaylistItem
import com.example.TIUMusic.Libs.YoutubeLib.models.SearchingInfo
import com.example.TIUMusic.Libs.YoutubeLib.models.SectionListRenderer
import com.example.TIUMusic.Libs.YoutubeLib.models.SongItem
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeContent
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.HomeItem
import com.example.TIUMusic.Libs.YoutubeLib.models.TIUMusic.parseSongArtists
import com.example.TIUMusic.Libs.YoutubeLib.models.VideoItem
import com.example.TIUMusic.Libs.YoutubeLib.models.YouTubeClient
import com.example.TIUMusic.Libs.YoutubeLib.models.response.SearchResponse
import com.example.TIUMusic.Libs.YoutubeLib.pages.ArtistPage
import com.example.TIUMusic.Libs.YoutubeLib.pages.RelatedPage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
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
    private val ytmusic: Ytmusic // Inject Ytmusic class (nếu dùng Hilt hoặc tạo instance thủ công)
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<SearchingInfo>>(emptyList())
    val searchResults: StateFlow<List<SearchingInfo>> = _searchResults

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private var _homeItems = MutableStateFlow<List<HomeItem>>(emptyList());
    val homeItems : StateFlow<List<HomeItem>> = _homeItems.asStateFlow();

    private var _homeContinuation = mutableIntStateOf(0);
    val homeContinuation = _homeContinuation.asIntState();

    fun performSearch(query: String){
        var videoInfos: List<SearchingInfo>
        Log.d("viewModelTest", "RUN")
        viewModelScope.launch {
            _loading.value = true
            try {
                withContext(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
                    Log.e("viewModelTest", "Lỗi trong coroutine: ${throwable.message}")
                }) {
                    val client = YouTubeClient.WEB_REMIX
                    val response = ytmusic.search(client = client, query).body<SearchResponse>()
//                    // Cấu hình JSON parser
//                    val json = Json {
//                        ignoreUnknownKeys = true
//                        isLenient = true
//                    }
//                    // Parse JSON
//                    val parsedResponse = json.decodeFromString<SearchResponse>(response)
//                    val parsedResponseString = parsedResponse.toString()
//                    // Phần còn lại của mã
//                    val maxLogSize = 1000
//                    for (i in 0..parsedResponseString.length / maxLogSize) {
//                        val start = i * maxLogSize
//                        var end = (i + 1) * maxLogSize
//                        end =
//                            if (end < parsedResponseString.length) end else parsedResponseString.length
//                        Log.d("messageReturn", parsedResponseString.substring(start, end))
//                    }
//                    Log.d("messageReturn", "ENDJSON")

                    videoInfos = extractVideoInfo(response)

                    // Chuyển đổi dữ liệu để phù hợp với định dạng mong muốn
                    val formattedResults = videoInfos.map { videoInfo ->
                        SearchingInfo(
                            title = videoInfo.title ?: "Unknown Title",
                            videoId = videoInfo.videoId ?: "Unknown ID",
                            artist = videoInfo.artist ?: "Unknown Artist",
                            artistId = videoInfo.artistId ?: "Unknown Artist ID"
                        )
                    }
                    // Gán giá trị mới cho _searchResults
                    _searchResults.value = formattedResults
                }
            } catch (e: Exception) {
                // Xử lý ngoại lệ ở đây
                Log.d("viewModelTest", "Error occurred: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
    // Hàm trích xuất thông tin video ID
    private fun extractVideoInfo(response: SearchResponse): List<SearchingInfo> {
        // Thông tin trả về
        val searchInfos = mutableListOf<SearchingInfo>()

        // Lấy tabs đầu tiên
        val listShelfRender = response.contents?.tabbedSearchResultsRenderer?.tabs?.firstOrNull()?.tabRenderer?.content?.sectionListRenderer?.contents
            ?:throw Exception(" No renderer")

        // Duyệt
        for (renderer in listShelfRender.take(3)){
            if(renderer.musicCardShelfRenderer == null && renderer.musicShelfRenderer != null){
                val contents = renderer.musicShelfRenderer.contents
                    ?: throw Exception(" - No content in Renderer found")
                Log.d(
                    "viewModelTest",
                    "Count musicResponsiveListItemRenderer size: ${renderer.musicShelfRenderer.contents.size}"
                )
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
                    searchInfos.add(
                        SearchingInfo(
                            title = songRender.text,
                            videoId = songRender.navigationEndpoint?.watchEndpoint?.videoId,
                            artist = artistRender[i].text,
                            artistId = artistRender[i].navigationEndpoint?.browseEndpoint?.browseId
                        )
                    )
                }
            }
        }
        return searchInfos
    }

    fun GetContinuation(context: Context) {
        _homeContinuation.intValue = 2;
        viewModelScope.launch {
            getHomeScreen(context).collect {
                _homeItems.value = it;
            }
        }
        _homeContinuation.intValue++;
    }

    private suspend fun getHomeScreen(context: Context) : Flow<List<HomeItem>> =
        flow {
            runCatching {
                YouTube.customQuery(browseId = "FEmusic_home")
                    .onSuccess { result ->
                        var continueParam =
                            result.contents?.singleColumnBrowseResultsRenderer?.tabs?.get(0)?.
                                tabRenderer?.content?.sectionListRenderer?.continuations?.get(0)?.nextContinuationData?.continuation
                        val data =
                            result.contents?.singleColumnBrowseResultsRenderer?.tabs?.get(0)?.tabRenderer?.content?.sectionListRenderer?.contents;
                        val list: ArrayList<HomeItem> = arrayListOf()
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
                        emit(list)
                    }
                    .onFailure { error ->
                        Log.e("YoutubeViewModel", error.message.toString());
                        emit(listOf());
                    }
            }
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
                                            ?.map {
                                                Artist(
                                                    name = it.name,
                                                    id = it.id,
                                                )
                                            }?.toMutableList()
                                    if (artists?.lastOrNull()?.id == null &&
                                        artists?.lastOrNull()?.name?.contains(Regex("\\d")) == true
                                    ) {
                                        runCatching { artists.removeAt(artists.lastIndex) }
                                            .onSuccess {
                                                Log.i(
                                                    "parse_mixed_content",
                                                    "Removed last artist"
                                                )
                                            }.onFailure {
                                                Log.e(
                                                    "parse_mixed_content",
                                                    "Failed to remove last artist"
                                                )
                                                it.printStackTrace()
                                            }
                                    }
                                    Log.w("Song", ytItem.toString())
                                    if (ytItem != null) {
                                        listContent.add(
                                            HomeContent(
                                                album =
                                                ytItem.album?.let {
                                                    Album(
                                                        name = it.name,
                                                        id = it.id
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
                                    if (artists?.lastOrNull()?.id == null &&
                                        artists?.lastOrNull()?.name?.contains(
                                            Regex("\\d"),
                                        ) == true
                                    ) {
                                        runCatching { artists.removeAt(artists.lastIndex) }
                                            .onSuccess {
                                                Log.i(
                                                    "parse_mixed_content",
                                                    "Removed last artist"
                                                )
                                            }.onFailure {
                                                Log.e(
                                                    "parse_mixed_content",
                                                    "Failed to remove last artist"
                                                )
                                                it.printStackTrace()
                                            }
                                    }
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
                    Log.w("parse_mixed_content", list.toString())
                }
            }
        }
        return list;
    }

}