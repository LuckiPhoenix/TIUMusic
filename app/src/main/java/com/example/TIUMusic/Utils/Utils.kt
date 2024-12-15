package com.example.TIUMusic.Utils

import android.content.Context
import android.net.Uri
import com.example.TIUMusic.MainActivity
import com.example.TIUMusic.SongData.MusicItem

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
) {
    class Success<T>(
        data: T,
    ) : Resource<T>(data)

    class Error<T>(
        message: String,
        data: T? = null,
    ) : Resource<T>(data, message)
}

fun getResource(name : String) : Uri {
    val pkgName: String = MainActivity.applicationContext.packageName;
    return Uri.parse("android.resource://$pkgName:$name");
}

fun nameToRID(name : String, type : String, context: Context) : Int? {
    val id = context.resources.getIdentifier(name, type, context.packageName);
    if (id == 0)
        return null;
    return id;
}

//Library Playlists
fun isPlaylistRandomUUID(id: String): Boolean {
    val uuidRegex = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$")
    return uuidRegex.matches(id)
}