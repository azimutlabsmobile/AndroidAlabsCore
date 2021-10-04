package com.alabs.core_application.domain

import com.alabs.core_application.data.network.ResultApi
import kotlinx.coroutines.flow.Flow


/**
 * Интерфейс для реализации useCase без входящего параметра
 * @param T параметр метода
 * @param V результат выполненного действия
 */
interface BaseNoneParamUseCase<out V : Any> {

    fun execute(): V
}


/**
 * Интерфейс для реализации useCase с входящем параметром
 * @param T параметр метода
 * @param V результат выполненного действия
 */
interface BaseUseCase<out V : Any> {

    fun execute(): V
}


/**
 * Интерфейс для реализации useCase с входящем параметром c suspend функцией
 * @param T параметр метода
 * @param V результат выполненного действия
 */
interface BaseSuspendUseCase<in I, out V : Any> {

    suspend fun execute(param: I): V
}

/**
 * Интерфейс для реализации useCase  без входящего параметром c suspend функцией
 * @param V результат выполненного действия
 */
interface BaseSuspendNonParamUseCase<out V : Any> {

    suspend fun execute(): V
}

/**
 * Интерфейс для реализации useCase с входящем параметром
 * @param T параметр метода
 * @param V  ResultApi результат выполненного действия
 */
interface CoreUseCase<in I, out V : Any> : BaseSuspendUseCase<I, ResultApi<V>> {

    override suspend fun execute(param: I): ResultApi<V>
}


/**
 * Интерфейс для реализации useCase без входящего параметром
 * @param V результат выполненного действия
 */
interface CoreNonParamUseCase<out V : Any> : BaseSuspendNonParamUseCase<ResultApi<V>> {

    override suspend fun execute(): ResultApi<V>
}


/**
 * Интерфейс для реализации useCase (flow) с входящем параметром
 * @param T параметр метода
 * @param V результат выполненного действия
 */
interface CoreFlowUseCase<in I, out V : Any> : BaseSuspendUseCase<I, Flow<V>> {

    override suspend fun execute(param: I): Flow<V>
}


/**
 * Интерфейс для реализации useCase (flow) без входящего параметром
 * @param T параметр метода
 * @param V результат выполненного действия
 */
interface CoreFlowNonParamUseCase<out V : Any> : BaseSuspendNonParamUseCase<Flow<V>> {

    override suspend fun execute(): Flow<V>
}