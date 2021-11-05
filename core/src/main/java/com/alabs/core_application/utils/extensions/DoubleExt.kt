package com.alabs.core_application.utils.extensions

import com.alabs.core_application.data.constants.CoreConstant.DOUBLE_ZERO
import kotlin.math.ceil

fun Double?.orZero() = this ?: 0.0

fun CharSequence?.toDoubleOrZero(): Double {
    return try {
        this.toString().replace(",", ".").toDouble()
    } catch (e: Exception) {
        DOUBLE_ZERO
    }
}

fun Double?.roundUp(): Int {
    return ceil(this.orZero()).toInt()
}