package com.alabs.lingver.store

import java.util.Locale

class InMemoryLocaleStore : LocaleStore {

    private var locale: Locale = Locale.getDefault()
    private var isFollowingSystemLocale = false

    override fun getLocale(): Locale = locale

    override fun persistLocale(locale: Locale) {
        this.locale = locale
    }

    override fun setFollowSystemLocale(value: Boolean) {
        isFollowingSystemLocale = value
    }

    override fun isFollowingSystemLocale(): Boolean {
        return isFollowingSystemLocale
    }
}
