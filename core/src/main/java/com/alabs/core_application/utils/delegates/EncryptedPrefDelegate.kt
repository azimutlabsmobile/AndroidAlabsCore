package com.alabs.core_application.utils.delegates

import com.alabs.core_application.data.prefs.SecurityDataSource
import org.koin.core.KoinComponent
import org.koin.core.inject

interface EncryptedPrefDelegate {
    fun getPref() : SecurityDataSource
}

class EncryptedPrefDelegateImpl : EncryptedPrefDelegate, KoinComponent{
    private val pref = inject<SecurityDataSource>()
    override fun getPref(): SecurityDataSource  = pref.value
}