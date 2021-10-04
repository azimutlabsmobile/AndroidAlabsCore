package com.alabs.core_application.utils

import android.text.TextUtils
import android.util.Patterns

/**
 * Валидация email
 * @param target email
 */
fun isValidEmail(target: CharSequence?) = if (TextUtils.isEmpty(target)) {
    false
} else {
    Patterns.EMAIL_ADDRESS.matcher(target?.toString().orEmpty()).matches()
}

/**
 * Валидация номер телефона
 * @param target номер телефона
 */
fun isValidPhoneNumber(target : CharSequence?) : Boolean{
    target ?: return  false

    return target.length >= 10
}