package com.alabs.core_application.utils

import com.google.gson.Gson

/**
 * Парсит json строку
 * @param json строка
 */
inline fun <reified T> parseJson(json: String?): T? = try {
    Gson().fromJson(json, T::class.java)
} catch (ex: Exception) {
    null
}