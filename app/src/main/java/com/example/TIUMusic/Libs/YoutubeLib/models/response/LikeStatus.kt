package com.example.TIUMusic.Libs.YTMusicScrapper.models.response

enum class LikeStatus {
    LIKE,
    DISLIKE,
    INDIFFERENT
}

fun String?.toLikeStatus(): LikeStatus {
    return when (this) {
        "LIKE" -> LikeStatus.LIKE
        "DISLIKE" -> LikeStatus.DISLIKE
        else -> LikeStatus.INDIFFERENT
    }
}