package com.alabs.core_application.utils.extensions

fun CharSequence?.empty() : String = this?.toString().orEmpty()