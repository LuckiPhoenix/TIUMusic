package com.example.TIUMusic.Libs.YoutubeLib

import com.example.TIUMusic.Libs.YTMusicScrapper.encoder.brotli
import com.example.TIUMusic.Libs.YTMusicScrapper.models.Context
import com.example.TIUMusic.Libs.YTMusicScrapper.models.WatchEndpoint
import com.example.TIUMusic.Libs.YTMusicScrapper.models.YouTubeClient
import com.example.TIUMusic.Libs.YTMusicScrapper.models.YouTubeLocale
import com.example.TIUMusic.Libs.YTMusicScrapper.models.body.AccountMenuBody
import com.example.TIUMusic.Libs.YTMusicScrapper.models.body.CreatePlaylistBody
import com.example.TIUMusic.Libs.YTMusicScrapper.models.body.EditPlaylistBody
import com.example.TIUMusic.Libs.YTMusicScrapper.models.body.GetQueueBody
import com.example.TIUMusic.Libs.YTMusicScrapper.models.body.LikeBody
import com.example.TIUMusic.Libs.YTMusicScrapper.models.body.NextBody
import com.example.TIUMusic.Libs.YTMusicScrapper.models.body.NotificationBody
import com.example.TIUMusic.Libs.YTMusicScrapper.utils.parseCookieString
import com.example.TIUMusic.Libs.YTMusicScrapper.utils.sha1
import com.example.TIUMusic.Libs.YoutubeLib.models.BrowseBody
import com.example.TIUMusic.Libs.YoutubeLib.models.FormData
import com.example.TIUMusic.Libs.YoutubeLib.models.GetSearchSuggestionsBody
import com.example.TIUMusic.Libs.YoutubeLib.models.PlayerBody
import com.example.TIUMusic.Libs.YoutubeLib.models.SearchBody
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.userAgent
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.protobuf.protobuf
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.Interceptor
import java.io.File
import java.lang.reflect.Type
import java.net.Proxy
import java.util.Locale

class Ytmusic {
    private var httpClient = createClient()

    var cacheControlInterceptor: Interceptor? = null
        set(value) {
            field = value
            httpClient.close()
            httpClient = createClient()
        }
    var forceCacheInterceptor: Interceptor? = null
        set(value) {
            field = value
            httpClient.close()
            httpClient = createClient()
        }
    var cachePath: File? = null
        set(value) {
            field = value
            httpClient = createClient()
        }

    var locale =
        YouTubeLocale(
            gl = Locale.getDefault().country,
            hl = Locale.getDefault().toLanguageTag(),
        )
    var visitorData: String = "Cgt6SUNYVzB2VkJDbyjGrrSmBg%3D%3D"
    var cookie: String? = null
        set(value) {
            field = value
            cookieMap = if (value == null) emptyMap() else parseCookieString(value)
        }
    private var cookieMap = emptyMap<String, String>()


    var proxy: Proxy? = null
        set(value) {
            field = value
            httpClient.close()
            httpClient = createClient()
        }

    @OptIn(ExperimentalSerializationApi::class)
    private fun createSpotifyClient() =
        HttpClient(OkHttp) {
            expectSuccess = true
            followRedirects = false
            if (cachePath != null) {
                engine {
                    config {
                        cache(
                            okhttp3.Cache(cachePath!!, 50L * 1024 * 1024),
                        )
                    }
                    if (cacheControlInterceptor != null) {
                        addNetworkInterceptor(cacheControlInterceptor!!)
                    }
                    if (forceCacheInterceptor != null) {
                        addInterceptor(forceCacheInterceptor!!)
                    }
                }
            }
            install(HttpCache)
            install(HttpSend) {
                maxSendCount = 100
            }
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            install(ContentNegotiation) {
                register(
                    ContentType.Text.Plain,
                    KotlinxSerializationConverter(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            explicitNulls = false
                            encodeDefaults = true
                        },
                    ),
                )
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                        encodeDefaults = true
                    },
                )
                protobuf(
                    ProtoBuf {
                        encodeDefaults = true
                    },
                )
            }
            install(ContentEncoding) {
                brotli(1.0F)
                gzip(0.9F)
                deflate(0.8F)
            }
            defaultRequest {
                url("https://api.spotify.com")
            }
        }

    @OptIn(ExperimentalSerializationApi::class)
    private fun createClient() =
        HttpClient(OkHttp) {
            expectSuccess = true
            if (cachePath != null) {
                engine {
                    config {
                        cache(
                            okhttp3.Cache(cachePath!!, 50L * 1024 * 1024),
                        )
                    }
                    if (cacheControlInterceptor != null) {
                        addNetworkInterceptor(cacheControlInterceptor!!)
                    }
                    if (forceCacheInterceptor != null) {
                        addInterceptor(forceCacheInterceptor!!)
                    }
                }
            }
            install(HttpCache)
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                        encodeDefaults = true
                    },
                )
                xml(
                    format =
                        XML {
                            xmlDeclMode = XmlDeclMode.Charset
                            autoPolymorphic = true
                        },
                    contentType = ContentType.Text.Xml,
                )
            }

            install(ContentEncoding) {
                brotli(1.0F)
                gzip(0.9F)
                deflate(0.8F)
            }

            if (proxy != null) {
                engine {
                    proxy = this@Ytmusic.proxy
                }
            }

            defaultRequest {
                url("https://music.youtube.com/youtubei/v1/")
            }
        }

    internal fun HttpRequestBuilder.mask(value: String = "*") = header("X-Goog-FieldMask", value)

    private fun HttpRequestBuilder.ytClient(
        client: YouTubeClient,
        setLogin: Boolean = false,
    ) {
        contentType(ContentType.Application.Json)
        headers {
            append("X-Goog-Api-Format-Version", "1")
            append(
                "X-YouTube-Client-Name",
                if (client != YouTubeClient.NOTIFICATION_CLIENT) client.clientName else "1",
            )
            append("X-YouTube-Client-Version", client.clientVersion)
            append(
                "x-origin",
                if (client != YouTubeClient.NOTIFICATION_CLIENT) "https://music.youtube.com" else "https://www.youtube.com",
            )
            append("X-Goog-Visitor-Id", visitorData)
            if (client == YouTubeClient.NOTIFICATION_CLIENT) {
                append("X-Youtube-Bootstrap-Logged-In", "true")
                append("X-Goog-Authuser", "0")
                append("Origin", "https://www.youtube.com")
            }
            if (client.referer != null) {
                append("Referer", client.referer)
            }
            if (setLogin) {
                cookie?.let { cookie ->
                    append("Cookie", cookie)
                    if ("SAPISID" !in cookieMap) return@let
                    val currentTime = System.currentTimeMillis() / 1000
                    val keyValue = cookieMap["SAPISID"] ?: cookieMap["__Secure-3PAPISID"]
                    println("keyValue: $keyValue")
                    val sapisidHash =
                        if (client != YouTubeClient.NOTIFICATION_CLIENT) {
                            sha1("$currentTime $keyValue https://music.youtube.com")
                        } else {
                            sha1("$currentTime $keyValue https://www.youtube.com")
                        }
                    append("Authorization", "SAPISIDHASH ${currentTime}_$sapisidHash")
                }
            }
        }
        userAgent(client.userAgent)
        parameter("key", client.api_key)
        parameter("prettyPrint", false)
    }

    // This is the youtube search bar
    suspend fun search(
        client: YouTubeClient,
        query: String? = null,
        params: String? = null,
        continuation: String? = null,
    ) = httpClient.post("search") {
            ytClient(client, true)
            setBody(
                SearchBody(
                    context = client.toContext(locale, visitorData),
                    query = query,
                    params = params,
                ),
            )
            parameter("continuation", continuation)
            parameter("ctoken", continuation)
    }

    suspend fun returnYouTubeDislike(videoId: String) =
        httpClient.get("https://returnyoutubedislikeapi.com/Votes?videoId=$videoId") {
            contentType(ContentType.Application.Json)
        }

    suspend fun player(
        client: YouTubeClient,
        videoId: String,
        playlistId: String?,
        cpn: String?,
    ) = httpClient.post("player") {
        ytClient(client, setLogin = true)
        setBody(
            PlayerBody(
                context =
                    client.toContext(locale, visitorData).let {
                        if (client == YouTubeClient.TVHTML5) {
                            it.copy(
                                thirdParty =
                                    Context.ThirdParty(
                                        embedUrl = "https://www.youtube.com/watch?v=$videoId",
                                    ),
                            )
                        } else {
                            it
                        }
                    },
                videoId = videoId,
                playlistId = playlistId,
                cpn = cpn,
            ),
        )
    }

    suspend fun getSuggestQuery(query: String) =
        httpClient.get("http://suggestqueries.google.com/complete/search") {
            contentType(ContentType.Application.Json)
            parameter("client", "firefox")
            parameter("ds", "yt")
            parameter("q", query)
        }

    private fun fromString(value: String?): List<String>? {
        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    suspend fun searchLrclibLyrics(
        q_track: String,
        q_artist: String,
    ) = httpClient.get("https://lrclib.net/api/search") {
        contentType(ContentType.Application.Json)
        headers {
            header(HttpHeaders.UserAgent, "PostmanRuntime/7.33.0")
            header(HttpHeaders.Accept, "*/*")
            header(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
            header(HttpHeaders.Connection, "keep-alive")
        }
        parameter("track_name", q_track)
        parameter("artist_name", q_artist)
    }

    suspend fun getYouTubeCaption(url: String) =
        httpClient.get(url) {
            contentType(ContentType.Text.Xml)
            headers {
                append(HttpHeaders.Accept, "text/xml; charset=UTF-8")
            }
        }

    suspend fun createYouTubePlaylist(
        title: String,
        listVideoId: List<String>?,
    ) = httpClient.post("playlist/create") {
        ytClient(YouTubeClient.WEB_REMIX, setLogin = true)
        setBody(
            CreatePlaylistBody(
                context = YouTubeClient.WEB_REMIX.toContext(locale, visitorData),
                title = title,
                videoIds = listVideoId,
            ),
        )
    }

    suspend fun editYouTubePlaylist(
        playlistId: String,
        title: String? = null,
    ) = httpClient.post("browse/edit_playlist") {
        ytClient(YouTubeClient.WEB_REMIX, setLogin = true)
        setBody(
            EditPlaylistBody(
                context = YouTubeClient.WEB_REMIX.toContext(locale, visitorData),
                playlistId = playlistId.removePrefix("VL"),
                actions =
                    listOf(
                        EditPlaylistBody.Action(
                            action = "ACTION_SET_PLAYLIST_NAME",
                            playlistName = title ?: "",
                        ),
                    ),
            ),
        )
    }

    suspend fun addItemYouTubePlaylist(
        playlistId: String,
        videoId: String,
    ) = httpClient.post("browse/edit_playlist") {
        ytClient(YouTubeClient.WEB_REMIX, setLogin = true)
        setBody(
            EditPlaylistBody(
                context = YouTubeClient.WEB_REMIX.toContext(locale, visitorData),
                playlistId = playlistId.removePrefix("VL"),
                actions =
                    listOf(
                        EditPlaylistBody.Action(
                            playlistName = null,
                            action = "ACTION_ADD_VIDEO",
                            addedVideoId = videoId,
                        ),
                    ),
            ),
        )
    }

    suspend fun removeItemYouTubePlaylist(
        playlistId: String,
        videoId: String,
        setVideoId: String,
    ) = httpClient.post("browse/edit_playlist") {
        ytClient(YouTubeClient.WEB_REMIX, setLogin = true)
        setBody(
            EditPlaylistBody(
                context = YouTubeClient.WEB_REMIX.toContext(locale, visitorData),
                playlistId = playlistId.removePrefix("VL"),
                actions =
                    listOf(
                        EditPlaylistBody.Action(
                            playlistName = null,
                            action = "ACTION_REMOVE_VIDEO",
                            removedVideoId = videoId,
                            setVideoId = setVideoId,
                        ),
                    ),
            ),
        )
    }

    /***
     * SponsorBlock testing
     * @author maxrave-dev
     */

    suspend fun getSkipSegments(videoId: String) =
        httpClient.get("https://sponsor.ajay.app/api/skipSegments/") {
            contentType(ContentType.Application.Json)
            parameter("videoID", videoId)
            parameter("category", "sponsor")
            parameter("category", "selfpromo")
            parameter("category", "interaction")
            parameter("category", "intro")
            parameter("category", "outro")
            parameter("category", "preview")
            parameter("category", "music_offtopic")
            parameter("category", "poi_highlight")
            parameter("category", "filler")
            parameter("service", "YouTube")
        }

    // Get playlist from channel
    suspend fun playlist(playlistId: String) =
        httpClient.post("browse") {
            ytClient(YouTubeClient.WEB_REMIX, !cookie.isNullOrEmpty())
            setBody(
                BrowseBody(
                    context =
                        YouTubeClient.WEB_REMIX.toContext(
                            locale,
                            visitorData,
                        ),
                    browseId = playlistId,
                    params = "wAEB",
                ),
            )
            parameter("alt", "json")
        }

    // Browse channel
    suspend fun browse(
        client: YouTubeClient,
        browseId: String? = null, // Artist homepage
        params: String? = null,
        continuation: String? = null,
        countryCode: String? = null,
        setLogin: Boolean = false,
    ) = httpClient.post("browse") {
        ytClient(client, if (setLogin) true else cookie != "" && cookie != null)

        if (countryCode != null) {
            setBody(
                BrowseBody(
                    context = client.toContext(locale, visitorData),
                    browseId = browseId,
                    params = params,
                    formData = FormData(listOf(countryCode)),
                ),
            )
        } else {
            setBody(
                BrowseBody(
                    context = client.toContext(locale, visitorData),
                    browseId = browseId,
                    params = params,
                ),
            )
        }
        parameter("alt", "json")
        if (continuation != null) {
            parameter("ctoken", continuation)
            parameter("continuation", continuation)
            parameter("type", "next")
        }
    }

    suspend fun nextCustom(
        client: YouTubeClient,
        videoId: String,
    ) = httpClient.post("next") {
        ytClient(client, setLogin = false)
        setBody(
            BrowseBody(
                context = client.toContext(locale, visitorData),
                browseId = null,
                params = "wAEB",
                enablePersistentPlaylistPanel = true,
                isAudioOnly = true,
                tunerSettingValue = "AUTOMIX_SETTING_NORMAL",
                playlistId = "RDAMVM$videoId",
                watchEndpointMusicSupportedConfigs =
                    WatchEndpoint.WatchEndpointMusicSupportedConfigs(
                        WatchEndpoint.WatchEndpointMusicSupportedConfigs.WatchEndpointMusicConfig(
                            musicVideoType = "MUSIC_VIDEO_TYPE_ATV",
                        ),
                    ),
            ),
        )
        parameter("alt", "json")
    }

    suspend fun next(
        client: YouTubeClient,
        videoId: String?,
        playlistId: String?,
        playlistSetVideoId: String?,
        index: Int?,
        params: String?,
        continuation: String? = null,
    ) = httpClient.post("next") {
        ytClient(client, setLogin = true)
        setBody(
            NextBody(
                context = client.toContext(locale, visitorData),
                videoId = videoId,
                playlistId = playlistId,
                playlistSetVideoId = playlistSetVideoId,
                index = index,
                params = params,
                continuation = continuation,
            ),
        )
    }

    suspend fun getSearchSuggestions(
        client: YouTubeClient,
        input: String,
    ) = httpClient.post("music/get_search_suggestions") {
        ytClient(client)
        setBody(
            GetSearchSuggestionsBody(
                context = client.toContext(locale, visitorData),
                input = input,
            ),
        )
    }

    suspend fun getQueue(
        client: YouTubeClient,
        videoIds: List<String>?,
        playlistId: String?,
    ) = httpClient.post("music/get_queue") {
        ytClient(client)
        setBody(
            GetQueueBody(
                context = client.toContext(locale, visitorData),
                videoIds = videoIds,
                playlistId = playlistId,
            ),
        )
    }

    suspend fun getSwJsData() = httpClient.get("https://music.youtube.com/sw.js_data")

    suspend fun accountMenu(client: YouTubeClient) =
        httpClient.post("account/account_menu") {
            ytClient(client, setLogin = true)
            setBody(AccountMenuBody(client.toContext(locale, visitorData)))
        }

    suspend fun scrapeYouTube(videoId: String) =
        httpClient.get("https://www.youtube.com/watch?v=$videoId") {
            headers {
                append(HttpHeaders.AcceptLanguage, locale.hl)
                append(HttpHeaders.ContentLanguage, locale.gl)
            }
        }

    suspend fun initPlayback(
        url: String,
        cpn: String,
        customParams: Map<String, String>? = null,
        playlistId: String?,
    ) = httpClient.get(url) {
        ytClient(YouTubeClient.ANDROID_MUSIC, true)
        parameter("ver", "2")
        parameter("c", "ANDROID_MUSIC")
        parameter("cpn", cpn)
        customParams?.forEach { (key, value) ->
            parameter(key, value)
        }
        if (playlistId != null) {
            parameter("list", playlistId)
            parameter("referrer", "https://music.youtube.com/playlist?list=$playlistId")
        }
    }

    suspend fun atr(
        url: String,
        cpn: String,
        customParams: Map<String, String>? = null,
        playlistId: String?,
    ) = httpClient.post(url) {
        ytClient(YouTubeClient.ANDROID_MUSIC, true)
        parameter("c", "ANDROID_MUSIC")
        parameter("cpn", cpn)
        customParams?.forEach { (key, value) ->
            parameter(key, value)
        }
        if (playlistId != null) {
            parameter("list", playlistId)
            parameter("referrer", "https://music.youtube.com/playlist?list=$playlistId")
        }
    }

    suspend fun getNotification() =
        httpClient.post("https://www.youtube.com/youtubei/v1/notification/get_notification_menu") {
            ytClient(YouTubeClient.NOTIFICATION_CLIENT, true)
            setBody(
                NotificationBody(
                    context = YouTubeClient.NOTIFICATION_CLIENT.toContext(locale, visitorData),
                ),
            )
        }

    suspend fun addToLiked(videoId: String) =
        httpClient.post("like/like") {
            ytClient(YouTubeClient.WEB_REMIX, true)
            setBody(
                LikeBody(
                    context = YouTubeClient.WEB_REMIX.toContext(locale, visitorData),
                    target = LikeBody.Target(videoId),
                ),
            )
        }

    suspend fun removeFromLiked(videoId: String) =
        httpClient.post("like/removelike") {
            ytClient(YouTubeClient.WEB_REMIX, true)
            setBody(
                LikeBody(
                    context = YouTubeClient.WEB_REMIX.toContext(locale, visitorData),
                    target = LikeBody.Target(videoId),
                ),
            )
        }
}