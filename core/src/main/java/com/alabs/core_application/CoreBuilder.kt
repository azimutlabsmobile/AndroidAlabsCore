package com.alabs.core_application

import android.app.Activity
import android.app.Application
import com.alabs.core_application.data.constants.CoreVariables.ACTIVITIES_SCREEN_ORIENTATION
import com.alabs.core_application.data.constants.CoreVariables.APOLLO_CACHE_ENABLED
import com.alabs.core_application.data.constants.CoreVariables.APOLLO_INTERCEPTORS
import com.alabs.core_application.data.constants.CoreVariables.BASE_APOLLO_URL
import com.alabs.core_application.data.constants.CoreVariables.BASE_URL
import com.alabs.core_application.data.constants.CoreVariables.BASIC_REFRESH_AUTH_HEADER
import com.alabs.core_application.data.constants.CoreVariables.IMAGE_URL
import com.alabs.core_application.data.constants.CoreVariables.IS_PRODUCTION
import com.alabs.core_application.data.constants.CoreVariables.LOGIN_ACTIVITY
import com.alabs.core_application.data.constants.CoreVariables.NETWORK_APOLLO_INTERCEPTORS
import com.alabs.core_application.data.constants.CoreVariables.OPERATOR
import com.alabs.core_application.data.constants.CoreVariables.REFRESH_TOKEN_END_POINT
import com.alabs.core_application.data.constants.CoreVariables.REST_INTERCEPTORS
import com.alabs.core_application.data.constants.CoreVariables.REST_NETWORK_INTERCEPTORS
import com.alabs.core_application.data.constants.CoreVariables.URLS_OF_UNNECESSARY_BEARER_TOKEN_ENDPOINTS
import com.alabs.core_application.data.constants.CoreVariables.WEB_URL
import okhttp3.Interceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module


class CoreBuilder(private val application: Application) {

    private var koinModule = arrayListOf<Module>()

    /**
     * Базовый url для retrofit
     * @param {String.()} строка с url для apollo
     */
    fun baseRetrofitUrl(block: () -> String) {
        BASE_URL = block()
    }

    /**
     * Базовый url для изображений
     * @param {String.()} строка с url для изображений
     */
    fun baseImageUrl(block: () -> String) {
        IMAGE_URL = block()
    }

    /**
     * url по вебу
     * @param {String.()} строка с url по веб версии аппки
     */
    fun webUrl(block: () -> String) {
        WEB_URL = block()
    }

    /**
     * Базовые url для клиента Apollo
     * @param {String.()} строка с url для apollo
     */
    fun baseApolloUrl(block: () -> String) {
        BASE_APOLLO_URL = block()
    }

    /**
     * Модуля для koin
     * @param {List.()} список с модулями
     */
    fun koinModule(block: () -> ArrayList<Module>) {
        koinModule = block()
    }

    /**
     * Список url которым не требуються header-ы
     * @param {List.()} список с url
     */
    fun endpointUrlsNecessaryForAuthBearer(block: () -> List<String>) {
        URLS_OF_UNNECESSARY_BEARER_TOKEN_ENDPOINTS = block()
    }

    /**
     * Базовый header для refresh token
     * @param {String.()} строка с базовым header-ом
     */
    fun baseHeaderRefreshToken(block: () -> String) {
        BASIC_REFRESH_AUTH_HEADER = block()
    }

    /**
     * передаем запрос для refresh token например "api/refresh/token"
     * @param {String.()} строка с end point
     */
    fun refreshTokenEndPoint(block: () -> String) {
        REFRESH_TOKEN_END_POINT = block()
    }


    /**
     * При испечении токена перенаправляем на данное activity
     * @param {Activity.()} объект activity
     */
    fun loginActivity(block: () -> Activity) {
        LOGIN_ACTIVITY = block()
    }

    /**
     * Передаем оператор мобильной связи, например TELE2, ALTEL
     * @param {String.()} передаем стринг
     */
    fun operator(block: () -> String?) {
        OPERATOR = block().orEmpty()
    }

    /**
     * Передаем флаг true если отправляем на демонстарцию или продакшен
     * Для разработки передаем false
     * Для корректной работы приложениея передаем true
     */
    fun isProduction(block: () -> Boolean) {
        IS_PRODUCTION = block()
    }

    /**
     * Добавление возможности кэшировать запросы apollo
     * Кэширование несовместимо с одновременной работой с [okhttp3.logging.HttpLoggingInterceptor]
     * При включенном кэширвании отключается логирование запросов
     */
    fun apolloCacheEnabled(block: () -> Boolean) {
        APOLLO_CACHE_ENABLED = block()
    }

    /**
     * Передаем список network интецепторов для graph QL
     */
    fun apolloNetworkInterceptors(block: () -> List<Interceptor>) {
        NETWORK_APOLLO_INTERCEPTORS = block()
    }

    /**
     * Передаем список интецепторов для graph QL
     */
    fun apolloInterceptors(block: () -> List<Interceptor>) {
        APOLLO_INTERCEPTORS = block()
    }


    /**
     * Передаем список network интецепторов для rest API
     */
    fun restNetworkInterceptors(block: () -> List<Interceptor>) {
        REST_NETWORK_INTERCEPTORS = block()
    }

    /**
     * Передаем список интецепторов для rest API
     */
    fun restInterceptors(block: () -> List<Interceptor>) {
        REST_INTERCEPTORS = block()
    }

    /**
     * Передаем ориентация всех активити
     */
    fun allActivitiesOrientation(block: () -> Int) {
        ACTIVITIES_SCREEN_ORIENTATION = block()
    }

    fun build() {
        startKoin {
            androidLogger()
            androidContext(this@CoreBuilder.application.applicationContext)
            modules(koinModule)
        }
    }
}