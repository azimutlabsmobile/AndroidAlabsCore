package com.alabs.core_application.utils.exeption

import com.apollographql.apollo.exception.ApolloException

/**
 * Испключение было написано для код ошибки 200,
 * в нашем случае бек отправляет ошибки в 200-ом коде
 */
class ApolloSuccessException(msg: String) : ApolloException(msg)