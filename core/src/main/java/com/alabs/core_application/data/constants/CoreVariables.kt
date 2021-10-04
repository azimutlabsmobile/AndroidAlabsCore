package com.alabs.core_application.data.constants

import android.app.Activity
import okhttp3.Interceptor

object CoreVariables {

    /**
     * Id фрагмента во всем приложении
     */
    var ID_FRAGMENT = -1

    /**
     * url для REST API
     */
    var BASE_URL = ""


    /**
     * url для изображений
     */
    var IMAGE_URL = ""

    /**
     * url по веб версии аппки
     */
    var WEB_URL = ""

    /**
     * url для graphQL
     */
    var BASE_APOLLO_URL = ""

    /**
     * Activity для авторизации
     */
    var LOGIN_ACTIVITY: Activity? = null

    /**
     * Для хранение url-ов которые не нуждаються в token
     */
    var URLS_OF_UNNECESSARY_BEARER_TOKEN_ENDPOINTS: List<String> = emptyList()

    /**
     * Базовые header для refresh token
     */
    var BASIC_REFRESH_AUTH_HEADER = ""

    /**
     * Запрос для refresh token
     */
    var REFRESH_TOKEN_END_POINT = ""

    /**
     * Тип оператора связи
     */
    var OPERATOR = ""

    /**
     * Ключ для sharedPreferences, чтобы не было конфликтов в локальных хранилищах
     */
    var PREF_SOURCES_LOCAL = OPERATOR

    /**
     * Флаг для проверки продовой версии приложения
     */
    var IS_PRODUCTION = true

    /**
     * Нужно ли кэшировать apollo запросы
     * Http кэширование несовместимо с работой с [okhttp3.logging.HttpLoggingInterceptor]
     * Поэтому, если передается true, то логирование запросов отключается
     */
    var APOLLO_CACHE_ENABLED = false

    /**
     * Список дополнительных интерцепторов для rest api
     */
    var REST_INTERCEPTORS: List<Interceptor>? = null

    /**
     * Список дополнительных network интерцепторов  для rest api
     */
    var REST_NETWORK_INTERCEPTORS: List<Interceptor>? = null

    /**
     * Список дополнительных интерцепторов для qraph QL
     */
    var APOLLO_INTERCEPTORS: List<Interceptor>? = null

    /**
     * Список дополнительных network интерцепторов для qraph QL
     */
    var NETWORK_APOLLO_INTERCEPTORS: List<Interceptor>? = null

    /**
     * Ориентация всех активити
     */
    var ACTIVITIES_SCREEN_ORIENTATION: Int? = null
}