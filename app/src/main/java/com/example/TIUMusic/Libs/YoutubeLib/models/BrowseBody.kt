package com.example.TIUMusic.Libs.YoutubeLib.models

import com.example.TIUMusic.Libs.YoutubeLib.models.Context
import com.example.TIUMusic.Libs.YoutubeLib.models.WatchEndpoint
import kotlinx.serialization.Serializable

@Serializable
data class BrowseBody(
    val context: Context,
    val browseId: String?,
    val params: String?,
    val formData: FormData? = null,
    val enablePersistentPlaylistPanel: Boolean? = null,
    val isAudioOnly: Boolean? = null,
    val tunerSettingValue: String? = null,
    val playlistId: String? = null,
    val watchEndpointMusicSupportedConfigs: WatchEndpoint.WatchEndpointMusicSupportedConfigs? = null
)