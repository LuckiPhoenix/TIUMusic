package com.example.TIUMusic.Libs.YoutubeLib

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.TIUMusic.Login.UserViewModel
import com.example.TIUMusic.R
import com.example.TIUMusic.ui.theme.BackgroundColor

@Composable
fun YoutubeLogin(navController: NavController, userViewModel: UserViewModel) {
    AndroidView(
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        factory = {
            WebView(it).apply{
                settings.javaScriptEnabled = true;
                settings.domStorageEnabled = true;
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        if (request != null) {
                            if (
                                request.url.toString().startsWith(context.getString(R.string.YOUTUBE_MUSIC_URL)) ||
                                request.url.toString().startsWith(context.getString(R.string.YOUTUBE_MOBILE_URL)) ||
                                request.url.toString().startsWith(context.getString(R.string.YOUTUBE_URL))
                            ) {
                                Log.d("YoutubeLogin", "Logged in");
                                return false;
                            }
                        }
                        return false;
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        if (url == context.getString(R.string.YOUTUBE_MUSIC_URL) ||
                            url == context.getString(R.string.YOUTUBE_MOBILE_URL) ||
                            url == context.getString(R.string.YOUTUBE_URL)) {
                            CookieManager.getInstance().getCookie(url)?.let {
                                YouTube.cookie = it
                            }
//                            WebStorage.getInstance().deleteAllData()
//
//                            // Clear all the cookies
//                            CookieManager.getInstance().removeAllCookies(null)
//                            CookieManager.getInstance().flush()

                            if (view != null) {
//                                view.clearCache(true)
//                                view.clearFormData()
//                                view.clearHistory()
//                                view.clearSslPreferences()
                            }
                            // Pop back stack
                            navController.popBackStack(0, inclusive =  true);
                            navController.navigate("login");
                        }
                    }
                }
                loadUrl(context.getString(R.string.YOUTUBE_LOG_IN_URL));
            }
        }
    )
}