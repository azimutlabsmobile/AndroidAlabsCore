package com.alabs.core_application.utils

import com.alabs.core_application.data.constants.CoreConstant
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.*

/**
 * Конвертируем дату из одного формата в другой
 * @param  currentDate дата для которой требуеться конвертация
 * @param oldDatePattern в каком формате сейчас дата
 * @param newDatePattern в какой формат нужно перевести дату
 * @return отформатированую дату
 */
fun convertDate(currentDate: String, oldDatePattern: String, newDatePattern: String): String = try {
    val oldDateInFormatting = SimpleDateFormat(oldDatePattern).parse(currentDate)
    val newDateInFormatting = SimpleDateFormat(newDatePattern).format(oldDateInFormatting)
    newDateInFormatting
} catch (ex: Exception) {
    CoreConstant.EMPTY
}

/**
 * Получание даты в нужном формате
 * @param date  дата
 * @param pattern в каком формате находиться дата
 * @return отформатированую дату
 */

fun getDate(date: Date?, pattern: String, locale: Locale = Locale.getDefault()): String = try {
    val formatDate = SimpleDateFormat(pattern, locale)
    formatDate.format(date ?: date?.toString().orEmpty())
} catch (ex: Exception) {
    CoreConstant.EMPTY
}

/**
 * Получение текущей даты в нужном формате
 * @param pattern в которум нужно вывести дату
 */
fun getCurrentDate(pattern: String): String {
    val formatter = SimpleDateFormat(pattern)
    val date = Date()
    return formatter.format(date)
}

/**
 * Форматирует Local Date в Date
 * @param localDate форматируемая дата
 * @return отформатированную дату
 */

fun asDate(localDate: LocalDate, pattern: String): Date? =
    SimpleDateFormat(pattern, Locale.getDefault()).parse(localDate.toString())


/**
 * Проверка лявляеться ли дата вчерашней
 * @param date дата для сравнения
 */

fun isYesterday(date: LocalDate?) = LocalDate.now().minusDays(1).compareTo(date) == 0

/**
 * Проверка лявляеться ли дата сегодняшней
 * @param date дата для сравнения
 */
fun isToday(date: LocalDate?) = LocalDate.now().compareTo(date) == 0