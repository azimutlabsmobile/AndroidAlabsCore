package com.alabs.core_application.utils.extensions

import com.alabs.core_application.data.constants.CoreConstant
import okhttp3.Request

/**
 * Добавляет стандартные заголовки для авторизационнной зоны
 * @param token токен пользователя
 * @return Request.Builder
 */
fun Request.Builder.addOAuthHeader(token: String?): Request.Builder {
    if (token?.length ?: -1 > 7) {
        return header(CoreConstant.AUTHORIZATION, token.orEmpty())
    }
    return this
}
