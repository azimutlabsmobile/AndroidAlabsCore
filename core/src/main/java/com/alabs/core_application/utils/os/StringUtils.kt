package com.alabs.core_application.utils.os

import com.alabs.core_application.data.constants.CoreConstant
import com.alabs.core_application.data.constants.CoreConstant.EMPTY
import com.alabs.core_application.data.constants.CorePatternConstant.PATTERN_FORMAT_AMOUNT_SPACE
import com.alabs.core_application.data.constants.CorePatternConstant.PATTERN_FORMAT_POINT_ZERO
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * Формирование из строки в числовое значение с двумя нулями
 */
fun formatWithZeroAmount(value: String?, currency: String = CoreConstant.EMPTY): String {
    val instance = NumberFormat.getNumberInstance(Locale.ENGLISH)
    val formatter = instance as? DecimalFormat
    if (value?.length ?: 0 <= 3) {
        formatter?.applyPattern(PATTERN_FORMAT_POINT_ZERO)
    } else {
        formatter?.applyPattern(PATTERN_FORMAT_AMOUNT_SPACE)
    }
    val result = try {
        var value = value
        value = value?.replace(",", ".")
        value = value?.replace("[^-0-9.]".toRegex(), "")
        formatter?.format(value?.toDouble())?.toString()?.replace(",", " ").orEmpty()
        if (value?.first().toString() == (".")) {
            value = "0$value"
        }
        value
    } catch (ex: Exception) {
        "0.00"
    }
    return "$result $currency"
}


/**
 * Добавление пробелов между элементами строки
 * 1000 -> 1 000
 * 1000.22 -> 1 000.22
 * -1000.50 -> -1 000.5
 */
@Deprecated("Не использовать будет удален замена [formatWithZeroAmount]")
fun formatWithSpaces(input: String?): String {

    if (input.isNullOrEmpty()) return EMPTY

    val stringToReturn = StringBuilder()
    var firstPart: String
    var secondPart: String?

    when {
        input.contains(",") -> {
            firstPart = input.substringBefore(",", EMPTY)
            secondPart = input.substringAfter(",", EMPTY)
        }
        input.contains(".") -> {
            firstPart = input.substringBefore(".", EMPTY)
            secondPart = input.substringAfter(".", EMPTY)
        }
        else -> {
            firstPart = input
            secondPart = null
        }
    }
    val startsWithMinus = firstPart.startsWith("-")
    firstPart = firstPart.replace("^0+".toRegex(), EMPTY).replace("-", EMPTY)
    secondPart?.let {
        secondPart = it.replace("0+\$".toRegex(), EMPTY)
    }

    if (firstPart.startsWith("0") || firstPart == EMPTY) {
        stringToReturn.append("0")
    } else {
        //добавление пробелов в первой части
        val firstPartLength = firstPart.length
        val digitOffset = firstPartLength.rem(3)
        if (startsWithMinus) stringToReturn.append("-")
        firstPart.forEachIndexed { index, char ->
            stringToReturn.append(char)
            if (firstPartLength > 3 && (index + 1 - digitOffset).rem(3) == 0 && (index + 1 != firstPartLength)) {
                stringToReturn.append(" ")
            }
        }
    }

    secondPart?.let {
        if (secondPart != EMPTY && secondPart != "0")
            stringToReturn.append(".").append(secondPart)

    }

    return stringToReturn.toString()
        .trim()
}

/**
 * Убирает маску у номер телефона, а также удаляет пробелы
 */
fun unmaskPhoneNumber(phoneNumber: String?): String? {
    phoneNumber ?: return CoreConstant.EMPTY

    if (phoneNumber.length <= 1) {
        return CoreConstant.EMPTY
    }
    var s = phoneNumber
    s = s.replace("+", CoreConstant.EMPTY)
    s = s.replace("\\D".toRegex(), CoreConstant.EMPTY)
    if (s.length > 10) {
        s = s.substring(1, s.length)
    }
    return s
}

/**
 * Урезает слово и заменяет точками
 * @param value слово
 * @param countReplaceWord сколько ссимволов нужно урезать
 */
fun makeShorterWord(value: String, countReplaceWord: Int): String {
    if (countReplaceWord == 0) {
        return value
    }

    return try {
        "${value.substring(0, value.length - countReplaceWord)}..."
    } catch (ex: Exception) {
        CoreConstant.EMPTY
    }
}