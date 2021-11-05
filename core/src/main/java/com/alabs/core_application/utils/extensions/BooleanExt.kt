package com.alabs.core_application.utils.extensions

/*
* Конвертация булена в инт
*/
fun Boolean.toInt() = if (this) 1 else 0

/*
* Если null то возвращет false
**/
fun Boolean?.orFalse() = this ?: false

/*
* Если null то возвращет true
**/
fun Boolean?.orTrue() = this ?: true
