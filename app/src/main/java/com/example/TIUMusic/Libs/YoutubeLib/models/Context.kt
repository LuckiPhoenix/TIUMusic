package com.example.TIUMusic.Libs.YoutubeLib.models

import kotlinx.serialization.Serializable

@Serializable
data class Context(
    val client: Client,
    val thirdParty: ThirdParty? = null,
) {
    @Serializable
    data class Client(
        val clientName: String,
        val clientVersion: String,
        val gl: String,
        val hl: String,
        val visitorData: String?,
        val osVersion: String?,
    )

    @Serializable
    data class ThirdParty(
        val embedUrl: String,
    )
}
