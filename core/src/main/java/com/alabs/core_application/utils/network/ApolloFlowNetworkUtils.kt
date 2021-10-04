package com.alabs.core_application.utils.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.alabs.core_application.data.constants.CoreVariables.APOLLO_CACHE_ENABLED
import com.alabs.core_application.utils.exeption.ApolloSuccessException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Функция для вызова стандартных GQL запросов с возможностью рабоать с Flow
 * @param call          - вызов GraphQL запроса
 * @param cachePolicy   - политика кэширования для запроса:
 * [com.apollographql.apollo.api.cache.http.HttpCachePolicy.ExpirePolicy]
 * Чтобы передать время жизни кеша, вызываем метод [com.apollographql.apollo.api.cache.http.HttpCachePolicy.ExpirePolicy.expireAfter]
 * К примеру, HttpCachePolicy.CACHE_FIRST.expireAfter(20,TimeUnit.SECONDS)
 * По умолчанию запрос не кэшируется
 * @return Flow
 */
suspend fun <A : Operation.Data, B : Any, C : Operation.Variables> ApolloClient.apolloFlowSafeApiCall(
    cachePolicy: HttpCachePolicy.ExpirePolicy? = null,
    call: suspend () -> Query<A, B, C>
): Flow<B?> = flow {
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
        emit(throw ApolloSuccessException(result.errors?.get(0)?.message.orEmpty()))
        return@flow
    }
    emit(result.data)
}