package com.alabs.core_application.utils.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.viewModelScope
import com.alabs.core_application.data.constants.CoreConstant
import com.alabs.core_application.data.network.ResultApi
import com.alabs.core_application.data.network.Status
import com.alabs.core_application.presentation.model.UIValidation
import com.alabs.core_application.presentation.viewModel.CoreAndroidViewModel
import com.alabs.core_application.utils.wrappers.EventWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.net.ssl.HttpsURLConnection


/**
 * запуск coroutine
 * Дает возможноть принимать свой тип в ответе ошибки
 * @param block suspend функция
 * @param result результат
 * @param errorBlock блок ошибки (по умолчанию String)
 * Если нужно использовать свой тив в ошибке применяем  [launchWithError]
 */
fun <T : Any> CoreAndroidViewModel.launch(
    block: suspend () -> ResultApi<T>,
    result: (T?) -> Unit,
    errorBlock: ((String?) -> Unit?)? = null
) {
    viewModelScope.launch(Dispatchers.Main) {
        statusLiveData.value = EventWrapper(Status.SHOW_LOADING)
        val value = block()
        unwrap(value, {
            result(it)
        }, errorBlock)
    }
}


/**
 * запуск coroutine c применением пользовательской ошибки с бэка
 * Дает возможноть принимать свой тип в ответе ошибки
 * @param block suspend функция
 * @param result результат
 * @param errorBlock блок ошибки с типом String
 */
fun <T : Any> CoreAndroidViewModel.unwrap(
    result: ResultApi<T>,
    successBlock: (T?) -> Unit,
    errorBlock: ((String) -> Unit?)? = null
) {
    when (result) {
        is ResultApi.Success -> {
            statusLiveData.value = EventWrapper(Status.HIDE_LOADING)
            successBlock(result.data)
        }
        is ResultApi.HttpError<*> -> {
            /**
             * Если лямбда для обработки ошибки не определена
             * Тогда выводим ошибку в liveData
             *
             * Бывают случаи когда ошибку нужно обработать
             */
            val error = result.error as? String ?: CoreConstant.EMPTY
            if (errorBlock == null) {
                _errorLiveData.value = EventWrapper(error)
            } else {
                errorBlock.invoke(error)
            }

            /**
             * Если приходит код 401 и ты имеем токен
             * отправляем в стутус редирект в экран логина или запрос нового токена
             */
            if (result.code == HttpsURLConnection.HTTP_UNAUTHORIZED /*&& !getPref().getAccessToken()
                    .isNullOrEmpty()*/
            ) {
                statusLiveData.value = EventWrapper(Status.REDIRECT_LOGIN)
                return
            }

            /**
             * В случае ошибки сервера получаем статус ошибки
             */
            statusLiveData.value = EventWrapper(Status.HIDE_LOADING)
            statusLiveData.value = EventWrapper(Status.ERROR)
        }
    }
}


/**
 * запуск coroutine c применением пользовательской ошибки с бэка
 * Дает возможноть принимать свой тип в ответе ошибки
 * @param block suspend функция
 * @param result результат
 * @param errorBlock блок ошибки (передаем свой тип)
 */
fun <T : Any, V : Any> CoreAndroidViewModel.launchWithError(
    block: suspend () -> ResultApi<T>,
    result: (T?) -> Unit,
    errorBlock: ((V?) -> Unit?)? = null
) {
    viewModelScope.launch(Dispatchers.Main) {
        statusLiveData.value = EventWrapper(Status.SHOW_LOADING)
        val value = block()
        unwrapWithError(value, {
            result(it)
        }, errorBlock)
    }
}


/**
 * Обработчик ошибок для viewModel используеться для того чтобы обработать ошибку с пользовательской model
 * @param result результат
 * @param successBlock получание результата (возврашает тип)
 * @param errorBlock получание ошибки (Возврашает тип переданный в [launchWithError])
 */
fun <T : Any, V : Any> CoreAndroidViewModel.unwrapWithError(
    result: ResultApi<T>,
    successBlock: (T?) -> Unit,
    errorBlock: ((V) -> Unit?)? = null
) {
    when (result) {
        is ResultApi.Success -> {
            statusLiveData.value = EventWrapper(Status.HIDE_LOADING)
            successBlock(result.data)
        }
        is ResultApi.HttpError<*> -> {
            val error = (result.error as? V) ?: return
            errorBlock?.invoke(error)

            /**
             * Если приходит код 401 и ты имеем токен
             * отправляем в стутус редирект в экран логина или запрос нового токена
             */
            if (result.code == HttpsURLConnection.HTTP_UNAUTHORIZED /*&& !getPref().getAccessToken()
                    .isNullOrEmpty()*/
            ) {
                statusLiveData.value = EventWrapper(Status.REDIRECT_LOGIN)
                return
            }

            /**
             * В случае ошибки сервера получаем статус ошибки
             */
            statusLiveData.value = EventWrapper(Status.HIDE_LOADING)
            statusLiveData.value = EventWrapper(Status.ERROR)
        }
    }
}


/**
 * Выводим сообщение об ошибке
 */
fun CoreAndroidViewModel.showError(msg: String) {
    _errorLiveData.value = EventWrapper(msg)
}

/**
 * Перенаправление на нужный фрагмент
 * @param [action] отправляем id action-а который приписываеться в nav графе
 * @param [msg] сообщение может быть любого характера
 */

fun CoreAndroidViewModel.redirectToFragment(@IdRes action: Int, bundle: Bundle? = null) {
    _redirectFragment.value = Pair(action, bundle)
}

/**
 * Выводим сообщение для определенного поля
 * @param errorMessage сообщение которые показываем на UI
 * @param type тип например Type.password (задаем в текущем модуле для определенного поля)
 */
fun CoreAndroidViewModel.showErrorByType(type: String?, errorMessage: String?) {
    _errorByTypeLiveData.value = EventWrapper(UIValidation(type.orEmpty(), errorMessage.orEmpty()))
}
