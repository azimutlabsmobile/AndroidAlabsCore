package com.alabs.core_application.utils.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.apollographql.apollo.exception.ApolloParseException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.alabs.core_application.data.constants.CoreVariables.APOLLO_CACHE_ENABLED
import com.alabs.core_application.data.network.ResultApi
import com.alabs.core_application.utils.exception.ApolloSuccessException
import java.io.IOException
import java.net.HttpURLConnection
import javax.net.ssl.SSLHandshakeException

/**
 * Функция для вызова стандартных GQL запросов
 * @param call          - вызов GraphQL запроса
 * @param cachePolicy   - политика кэширования для запроса:
 * [com.apollographql.apollo.api.cache.http.HttpCachePolicy.ExpirePolicy]
 * Чтобы передать время жизни кеша, вызываем метод [com.apollographql.apollo.api.cache.http.HttpCachePolicy.ExpirePolicy.expireAfter]
 * К примеру, HttpCachePolicy.CACHE_FIRST.expireAfter(20,TimeUnit.SECONDS)
 * По умолчанию запрос не кэшируется
 * @return ResultApi
 */
suspend fun <A : Operation.Data, B : Any, C : Operation.Variables> ApolloClient.apolloSafeApiCall(
    cachePolicy: HttpCachePolicy.ExpirePolicy? = null,
    call: suspend () -> Query<A, B, C>
): ResultApi<B> {
    return try {
        var queryCall = query(call.invoke())

        if (cachePolicy != null && APOLLO_CACHE_ENABLED) {
            queryCall = queryCall.toBuilder()
                .httpCachePolicy(cachePolicy)
                .responseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
                .build()
        }

        val result = queryCall.toDeferred().await()

        val isNullError = result.errors?.isNullOrEmpty() ?: true
        if (!isNullError) {
            throw ApolloSuccessException(result.errors?.get(0)?.message.orEmpty())
        }
        ResultApi.Success(result.data)
    } catch (e: ApolloException) {
        handleApolloException(e)
    }
}

/**
 * Обработка ошибок. Обработка ошибок для flow [handleError]
 */
fun <T : Any> handleApolloException(e: ApolloException): ResultApi<T> {
    return when (e) {
        is ApolloHttpException -> when (e.code()) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> ResultApi.HttpError(
                "Срок действия сессии истек",
                e.code()
            )
            HttpURLConnection.HTTP_NOT_FOUND -> ResultApi.HttpError(
                "Данного запроса не существует",
                e.code()
            )
            HttpURLConnection.HTTP_SERVER_ERROR -> ResultApi.HttpError(
                "Внутренняя ошибка сервера",
                e.code()
            )
            else -> ResultApi.HttpError("Ошибка сервера", e.code())
        }

        is ApolloNetworkException -> ResultApi.HttpError("Проверьте подключение к интернету",
            HttpURLConnection.HTTP_GATEWAY_TIMEOUT
        )
        is ApolloSuccessException -> ResultApi.HttpError(e.message.orEmpty())
        is SSLHandshakeException -> ResultApi.HttpError("Ошибка сертификата")
        is ApolloParseException -> ResultApi.HttpError("Невозможно распарсить данные")
        is IOException -> ResultApi.HttpError(e.message, HttpURLConnection.HTTP_INTERNAL_ERROR)
        else -> ResultApi.HttpError("Неизветная ошибка")
    }
}