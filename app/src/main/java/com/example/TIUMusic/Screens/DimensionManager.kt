package com.example.TIUMusic.Screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
Không phận sự cấm vào
Tuy nhiên có Visual Bug thì feel free to edit
if you know what you do
 */


enum class WindowSize {
    COMPACT,    // Phones
    MEDIUM      // Tablets
}

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> WindowSize.COMPACT
        else -> WindowSize.MEDIUM
    }
}

object Dimensions {
    @Composable
    fun contentPadding() = when (rememberWindowSize()) {
        WindowSize.COMPACT -> 16.dp
        WindowSize.MEDIUM -> 28.dp
    }

    @Composable
    fun itemSpacing() = when (rememberWindowSize()) {
        WindowSize.COMPACT -> 8.dp
        WindowSize.MEDIUM -> 14.dp
    }

    @Composable
    fun topBarExpandedHeight() = when (rememberWindowSize()) {
        WindowSize.COMPACT -> 128.dp
        WindowSize.MEDIUM -> 162.dp
    }

    @Composable
    fun topBarCollapsedHeight() = when (rememberWindowSize()) {
        WindowSize.COMPACT -> 86.dp
        WindowSize.MEDIUM -> 92.dp
    }

    @Composable
    fun expandedTitleSize() = when (rememberWindowSize()) {
        WindowSize.COMPACT -> 32.sp
        WindowSize.MEDIUM -> 42.sp
    }

    @Composable
    fun collapsedTitleSize() = when (rememberWindowSize()) {
        WindowSize.COMPACT -> 16.sp
        WindowSize.MEDIUM -> 22.sp
    }
}