package com.alabs.core_application.utils.network

import com.alabs.core_application.data.network.ResultApi


import com.apollographql.apollo.exception.ApolloHttpException
import com.google.gson.JsonParseException
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
 *
 */
fun <T, V : Any> Flow<T>.handleTypeError(bloc: suspend (Int, ResultApi<V>) -> Unit,  customResponseError: NetworkErrorHttpPrinter<V>? ) = catch {


    when (val exception = it as? Exception) {
        /**
         * Исключения для graphQl
         */
        is ApolloHttpException -> when (exception.code()) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> bloc(
                exception.code(),
                ResultApi.HttpError("Срок действия сессии истек")
            )
            HttpURLConnection.HTTP_NOT_FOUND -> bloc(
                exception.code(),
                ResultApi.HttpError("Данного запроса не существует")
            )
            else -> bloc(exception.code(), ResultApi.HttpError("Ошибка сервера"))
        }

        is ApolloSuccessException -> bloc(-1, ResultApi.HttpError(exception.message.orEmpty()))


        /**
         * Общие исключения
         */
        is HttpException -> handleHttpTypeFlowException<V>(
            exception,
            bloc,
            customResponseError
        )
        is SocketTimeoutException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            ResultApi.HttpError("Сервер не отвечает")
        )
        is SSLHandshakeException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            ResultApi.HttpError("Возникли проблемы с сертификатом")
        )
        is JsonParseException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            ResultApi.HttpError("Ошибка обработки запроса")
        )
        is EOFException, is ApolloHttpException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            ResultApi.HttpError("Ошибка загрузки, попробуйте еще раз")
        )
        is ConnectException,
        is UnknownHostException -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            ResultApi.HttpError("Возникли проблемы с интернетом")
        )
        else -> bloc(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            ResultApi.HttpError(exception?.localizedMessage.orEmpty())
        )
    }
}

/**
 * Обработка http ошибок  для flow
 */
private suspend fun <V : Any> handleHttpTypeFlowException(
    e: HttpException,
    bloc: suspend (Int, ResultApi<V>) -> Unit,
    customResponseError: NetworkErrorHttpPrinter<V>?
) {
    val errorBody = e.response()?.errorBody()?.string().orEmpty()
    when (e.code()) {
        HttpURLConnection.HTTP_UNAUTHORIZED -> {
            val reason = "Срок действия сессии истек"
            bloc(e.code(), ResultApi.HttpError(customResponseError?.print(errorBody, reason)))
        }
        else -> {
            bloc(e.code(), ResultApi.HttpError(customResponseError?.print(errorBody, "Ошибка")))
        }
    }
}
