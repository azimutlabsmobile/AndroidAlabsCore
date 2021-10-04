package com.alabs.core_application.utils.extensions

import android.app.Application
import com.alabs.core_application.CoreBuilder
import com.alabs.lingver.Lingver
import java.util.*

/**
 * Иницальзация core в Application классе
 */
fun Application.coreBuilder(block: CoreBuilder.() -> Unit) = CoreBuilder(this).apply(block).build()

/**
 * Дает возможность использовать язык во всем приложении
 */
fun Application.initLanguage() {
    Lingver.init(this, Locale.getDefault())
}