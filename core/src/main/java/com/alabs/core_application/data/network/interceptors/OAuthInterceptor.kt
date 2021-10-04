package com.alabs.core_application.data.network.interceptors

import android.provider.Telephony.Carriers.BEARER
import com.google.gson.Gson
import com.alabs.core_application.data.constants.CoreConstant.GRANT_TYPE_REFRESH_TOKEN
import com.alabs.core_application.data.constants.CoreVariables.BASE_URL
import com.alabs.core_application.data.constants.CoreVariables.BASIC_REFRESH_AUTH_HEADER
import com.alabs.core_application.data.constants.CoreVariables.OPERATOR
import com.alabs.core_application.data.constants.CoreVariables.REFRESH_TOKEN_END_POINT
import com.alabs.core_application.data.constants.CoreVariables.URLS_OF_UNNECESSARY_BEARER_TOKEN_ENDPOINTS
import com.alabs.core_application.data.prefs.SecurityDataSource
import com.alabs.core_application.data.models.AuthRefreshTokenDTO
import com.alabs.core_application.data.models.RefreshTokenRequestDTO
import com.alabs.core_application.utils.extensions.addOAuthHeader
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.IOException
import java.net.HttpURLConnection

@Deprecated("Впредь такого рода интерцепторы используем локально")
class OAuthInterceptor : Interceptor, KoinComponent {

    private val pref by inject<SecurityDataSource>()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val builder = originalRequest.newBuilder()
        /**
         * Если запрос находиться не в списке исключений тогда не
         * добаляем токен к заголовку
         */
        if (!checkUnnecessaryUrl(originalRequest)) {
            builder.addOAuthHeader("$BEARER ${pref.getAccessToken()}")
        }
        val response = chain.proceed(builder.build())


        when {
            /**
             * Если энпоин не находиться в списке исключений и получаем 401 ошибку
             */
            !checkUnnecessaryUrl(originalRequest) && response.code == HttpURLConnection.HTTP_UNAUTHORIZED
            -> {
                val refreshToken = pref.getRefreshToken().orEmpty()

                /**
                 * Если рефрешь токен пустой перекидываем на страницу ввода пароля и логина
                 */
                if (refreshToken.isEmpty()) {
                    return call401AndClearUserData(chain)
                }

                /**
                 * Формируем модель для refresh token
                 */
                val refreshTokenBody = RefreshTokenRequestDTO(
                    refreshToken,
                    GRANT_TYPE_REFRESH_TOKEN,
                    OPERATOR
                )

                /**
                 * Отправляем запрос на refresh token
                 */
                val body = Gson().toJson(refreshTokenBody).toString().toRequestBody()
                val refreshTokenRequest = originalRequest
                    .newBuilder()
                    .post(body)
                    .url(BASE_URL + REFRESH_TOKEN_END_POINT)
                    .addOAuthHeader(BASIC_REFRESH_AUTH_HEADER)
                    .build()

                val refreshResponse = chain.proceed(refreshTokenRequest)

                /**
                 * При успешном запросе записываем данные в prefs
                 * в противном слуае чистим данные и отправляем на экран логина
                 */
                if (refreshResponse.isSuccessful) {
                    val refreshedToken = Gson().fromJson(
                        refreshResponse.body?.string(),
                        AuthRefreshTokenDTO::class.java
                    )
                    pref.setAccessToken(refreshedToken.access_token)
                    pref.setRefreshToken(refreshedToken.refresh_token)
                    pref.setSession(refreshedToken?.session.orEmpty())
                    val token = refreshedToken.access_token
                    val newCall =
                        originalRequest.newBuilder().addOAuthHeader("$BEARER $token").build()
                    return chain.proceed(newCall)
                } else {
                    call401AndClearUserData(chain)
                }
            }
            /**
             * Если эндпоинт находиться в списке исключений и ловим 401 ошибку програмно изменяем ее на 500
             */
            checkUnnecessaryUrl(originalRequest) && response.code == HttpURLConnection.HTTP_UNAUTHORIZED -> {
                return response.newBuilder().code(HttpURLConnection.HTTP_INTERNAL_ERROR).build()
            }
        }

        return response
    }

    /**
     * Собераем url из сегментов для того чтобы сразнить есть ли собранный url
     * в списке исключений которому не требуеться токен
     * @param originalRequest
     */
    private fun checkUnnecessaryUrl(originalRequest: Request): Boolean {
        var isUnnecessaryUrl = false
        URLS_OF_UNNECESSARY_BEARER_TOKEN_ENDPOINTS.forEach {
            if(originalRequest.url.toString().contains(it)) {
                isUnnecessaryUrl = true
            }
        }
        return isUnnecessaryUrl
    }

    /**
     * Так как сервер всегда возращает code 200 програмно вызываем ошибку 401 для
     * того что-бы направить пользователя на страницу логина
     * Также происходит очистка все данных о пользователе
     * @param chain Interceptor.Chain
     */
    private fun call401AndClearUserData(chain: Interceptor.Chain): Response {
        pref.clearAuthorizedUserData()
        return chain.proceed(chain.request()).newBuilder()
            .code(HttpURLConnection.HTTP_UNAUTHORIZED).build()
    }
}