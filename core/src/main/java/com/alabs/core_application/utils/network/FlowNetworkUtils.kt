package com.alabs.core_application.utils.network

import com.apollographql.apollo.exception.ApolloHttpException
import com.google.gson.JsonParseException
import com.alabs.core_application.data.network.networkPrinter.ErrorHttpResponse
import com.alabs.core_application.data.network.networkPrinter.NetworkErrorHttpPrinter
import com.alabs.core_application.utils.exeption.ApolloSuccessException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.io.EOFException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException


/**
 * Обработчик ошибок для работы с flow (холодным потоком)
 */
fun <T> Flow<T>.handleError(bloc: suspend (Int, String) -> Unit) = catch {


    when (val exception = it as? Exception) {
        /**
         * Исключения для graphQl
         */
        is ApolloHttpException -> when (exception.code()) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> bloc(
                exception.code(),
                "Срок действия сессии истек"
            )
            HttpURLConnection.HTTP_NOT_FOUND -> bloc(
                exception.code(),
                "Данного запроса не существует"
            )
            else -> bloc(exception.code(), "Ошибка сервера")
        }

        is ApolloSuccessException -> bloc(-1, exception.message.orEmpty())


        /**
         * Общие исключения
         */
        is HttpException -> handleHttpFlowException(
            exception,
            bloc,
            ErrorHttpResponse()
        )
        is SocketTimeoutException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "Сервер не отвечает"
        )
        is SSLHandshakeException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "Возникли проблемы с сертификатом"
        )
        is JsonParseException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "Ошибка обработки запроса"
        )
        is EOFException, is ApolloHttpException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "Ошибка загрузки, попробуйте еще раз"
        )
        is ConnectException,
        is UnknownHostException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "Возникли проблемы с интернетом"
        )
        else -> bloc(HttpURLConnection.HTTP_INTERNAL_ERROR, exception?.localizedMessage.orEmpty())
    }
}

/**
 * Обработка http ошибок  для flow
 */
private suspend fun handleHttpFlowException(
    e: HttpException,
    bloc: suspend (Int, String) -> Unit,
    customResponseError: NetworkErrorHttpPrinter<String>?
) {
    val errorBody = e.response()?.errorBody()?.string().orEmpty()
    when (e.code()) {
        HttpURLConnection.HTTP_UNAUTHORIZED -> {
            val reason = "Срок действия сессии истек"
            bloc(e.code(), customResponseError?.print(errorBody, reason).orEmpty())
        }
        else -> {
            bloc(e.code(), customResponseError?.print(errorBody, "Ошибка").orEmpty())
        }
    }
}
