package com.alabs.core_application.utils.extensions

import android.annotation.SuppressLint
import android.webkit.WebView

/**
 * Created by Yergali Zhakhan on 5/13/20.
 */

@SuppressLint("SetJavaScriptEnabled")
fun WebView.init(url: String) {
    this.loadUrl(url)
    this.settings.builtInZoomControls = true
    this.settings.setSupportZoom(true)
    this.settings.useWideViewPort = true
    this.settings.javaScriptEnabled = true
}