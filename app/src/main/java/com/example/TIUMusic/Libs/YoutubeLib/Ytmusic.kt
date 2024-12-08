package com.example.TIUMusic.Libs.YoutubeLib

import com.example.TIUMusic.Libs.YoutubeLib.encoder.brotli
import com.example.TIUMusic.Libs.YoutubeLib.models.BrowseBody
import com.example.TIUMusic.Libs.YoutubeLib.models.FormData
import com.example.TIUMusic.Libs.YoutubeLib.models.SearchBody
import com.example.TIUMusic.Libs.YoutubeLib.models.YouTubeClient
import com.example.TIUMusic.Libs.YoutubeLib.models.YouTubeLocale
import com.example.TIUMusic.Libs.YoutubeLib.models.body.NextBody
import com.example.TIUMusic.Libs.YoutubeLib.utils.parseCookieString
import com.example.TIUMusic.Libs.YoutubeLib.utils.sha1
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.Interceptor
import java.io.File
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
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
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

    suspend fun getSuggestQuery(query: String) =
        httpClient.get("http://suggestqueries.google.com/complete/search") {
            contentType(ContentType.Application.Json)
            parameter("client", "firefox")
            parameter("ds", "yt")
            parameter("q", query)
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
}