package com.example.TIUMusic.Libs.YTMusicScrapper.extension

import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> T.asMap() : Map<String, Any?> {
    val props = T::class.memberProperties.associateBy { it.name }
    return props.keys.associateWith { props[it]?.get(this) }
}