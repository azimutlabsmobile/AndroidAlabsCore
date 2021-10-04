package com.alabs.core_application.utils.delegates

import android.content.Context
import android.os.Build
import com.alabs.core_application.data.constants.CoreConstant
import java.util.*

class LocaleDelegate(
    private val context: Context
) {
    fun getLocale(): String {
        return try {
            val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales.get(0)
            } else {
                context.resources.configuration.locale
            }

            locale.language
        } catch (e: Exception) {
            e.printStackTrace()
            CoreConstant.EMPTY
        }
    }
}