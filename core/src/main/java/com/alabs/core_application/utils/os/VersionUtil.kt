package com.alabs.core_application.utils.os

import android.os.Build

/**
 * Возвращает версию android
 */
fun getOSVersion(): Int {
    var osVersion = 0
    val androidOS = Build.VERSION.RELEASE
    val data = androidOS.split(".")
    if (data.isNotEmpty()) {
        osVersion = data[0].toInt()
    }
    return osVersion
}