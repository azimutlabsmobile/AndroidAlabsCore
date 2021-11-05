package com.alabs.core_application.utils.extensions

import android.os.Build
import android.text.Html
import com.alabs.core_application.data.constants.CoreConstant.EMPTY
import org.json.JSONObject
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Получаем удобочитаемый текст из html
 * Использовать если нужен только сам текст (форматирование и некоторые куски контента обрезаются,
 * к примеру, bullet point-ы)
 */
fun String?.fromHtml() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)?.toString().orEmpty()
} else {
    Html.fromHtml(this)?.toString().orEmpty()
}

fun String.remove(strToRemove: String): String {
    return this.replace(strToRemove, EMPTY)
}

fun String.containsAll(vararg list: String): Boolean {
    var containsCounter = 0
    list.forEach {
        if (this.contains(it)) containsCounter++
    }
    return containsCounter == list.size
}

fun String.containsAny(vararg list: String): Boolean {
    list.forEach {
        if (this.contains(it)) return true
    }
    return false
}

fun String.toJsonObject(): JSONObject{
    return JSONObject(this)
}

/**
 * Задаем правильный формат отображения для часов и минут
 * Пример: 0 -> 00, 9 -> 09
 */
fun String.toCorrectTimeUnitFormat() : String{
    if(this == "0")
        return "${this}0"
    else if(this.length == 1 && this.toInt()<10)
        return "0${this}"

    return this
}

/**
 * Преобразуем строку в секундах на минуты
 */
fun String.secondsToMinutes(): String {
    if(this.isNotEmpty()) {
        val seconds = this.toInt()
        val minutes = seconds / 60
        val format = DecimalFormat()
        format.roundingMode = RoundingMode.HALF_EVEN
        return format.format(minutes).toString()
    }
    return EMPTY
}

/**
 *  Получаем только цифры в строке
 */
fun String.getOnlyDigits() = this.filter{ it.isDigit() }
