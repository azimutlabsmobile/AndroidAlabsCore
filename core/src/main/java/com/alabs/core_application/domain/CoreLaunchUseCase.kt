package com.alabs.core_application.domain

import com.alabs.KDispatcher.IKDispatcher
import com.alabs.core_application.utils.delegates.CoreCoroutine
import com.alabs.core_application.utils.delegates.CoreCoroutineDelegate

/**
 * В случае если нужно отловить запрос в UseCase и преобразовать данный необходимо воспользоваться данным классом
 * @param T параметр метода
 * @param V результат выполненного действия
 */
abstract class CoreLaunchUseCase<in T, out V> : CoreCoroutine by CoreCoroutineDelegate(),
    IKDispatcher {

    abstract fun execute(param: T, result: ((V) -> Unit))
}


/**
 * В случае если нужно отловить запрос в UseCase и преобразовать данный необходимо воспользоваться данным классом
 * @param V результат выполненного действия
 */
abstract class CoreNonParamLaunchUseCase<out V> : CoreCoroutine by CoreCoroutineDelegate(),
    IKDispatcher {

    abstract fun execute(result: ((V) -> Unit))
}

/**
 * В случае если нужно отловить запрос в UseCase и преобразовать данный необходимо воспользоваться данным классом
 * @param T Входящий параметр
 * @param V результат выполненного действия
 * @param R ошибка
 */
abstract class CoreLaunchWithErrorUseCase< in T, out V, out R> : CoreCoroutine by CoreCoroutineDelegate(),
    IKDispatcher {
    abstract fun execute(param: T, result: ((V) -> Unit), error : ((R) -> Unit))
}


