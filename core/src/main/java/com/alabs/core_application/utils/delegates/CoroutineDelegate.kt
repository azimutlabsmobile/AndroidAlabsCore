package com.alabs.core_application.utils.delegates

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alabs.core_application.BuildConfig
import com.alabs.core_application.data.constants.CoreConstant
import com.alabs.core_application.data.network.LoadingType
import com.alabs.core_application.data.network.ResultApi
import com.alabs.core_application.data.network.Status
import com.alabs.core_application.data.network.networkPrinter.NetworkErrorHttpPrinter
import com.alabs.core_application.presentation.model.UIValidation
import com.alabs.core_application.utils.event.SingleLiveEvent
import com.alabs.core_application.utils.network.handleError
import com.alabs.core_application.utils.network.handleTypeError
import com.alabs.core_application.utils.wrappers.EventWrapper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.net.ssl.HttpsURLConnection


interface CoreCoroutine {

    val scope: CoroutineScope

    /**
     * Вывод ошибки при обрабоке http запросов
     */
    val errorEventLiveData: LiveData<EventWrapper<String>>

    /**
     * Статус при http запросе
     */
    val statusEventLiveData: LiveData<EventWrapper<Status>>

    /**
     * Вывод ошибок для конкретного поля
     */
    val errorEventByTypeLiveData: LiveData<EventWrapper<UIValidation>>


    /**
     * Вывод ошибок с кодом ошибки
     */
    val errorEventByCodeLiveData: LiveData<EventWrapper<Pair<Int, String>>>

    /**
     * Открытие фрагмента
     */
    val redirectEventFragment: LiveData<EventWrapper<Pair<Int, Bundle?>>>

    /**
     * Вывод сообщения
     */
    val successMessageEventLiveData: LiveData<EventWrapper<String>>


    /**
     * Вызов програмно прогресс бара
     */
    val loadingByTypeEvent: LiveData<EventWrapper<Pair<String, Boolean>>>


    /**
     * запуск coroutine
     * Дает возможноть принимать свой тип в ответе ошибки
     * @param block suspend функция
     * @param result результат
     * @param errorBlock блок ошибки (по умолчанию String)
     * @param isPullToRefresh если перезагрузка произошла через свайп тогда передаем true
     * Если нужно использовать свой тив в ошибке применяем  [launchWithError]
     */
    fun <T : Any> launch(
        block: suspend () -> ResultApi<T>,
        result: (T?) -> Unit,
        errorBlock: ((String?) -> Unit?)? = null,
        errorWithCodeBlock: ((Int,String?) -> Unit?)? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        loadingType: LoadingType = LoadingType.DEFAULT,
        loading: ((Boolean) -> Unit?)? = null
    )


    /**
     * Запусе холодного потока (flow)
     * @param requestFlow запрос в виде suspend функции
     * @param result результат
     * @param errorBlock блок ошибки (по умолчанию String)
     * @param isPullToRefresh если перезагрузка произошла через свайп тогда передаем true
     * Для дополнительной информации https://kotlinlang.org/docs/reference/coroutines/flow.html
     */
    fun <T> launchFlow(
        requestFlow: suspend () -> Flow<T>,
        result: (T) -> Unit,
        errorBlock: ((String?) -> Unit?)? = null,
        errorWithCodeBlock: ((Int,String?) -> Unit?)? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        loadingType: LoadingType = LoadingType.DEFAULT,
        loading: ((Boolean) -> Unit?)? = null
    )

    /**
     * Запусе холодного потока (flow) обработкой ошибки
     * @param requestFlow запрос в виде suspend функции
     * @param result результат
     * @param errorBlock блок ошибки (по умолчанию String)
     * @param isPullToRefresh если перезагрузка произошла через свайп тогда передаем true
     * Для дополнительной информации https://kotlinlang.org/docs/reference/coroutines/flow.html
     */
    fun <T : Any, V : Any> launchFlowWithError(
        requestFlow: suspend () -> Flow<T>,
        result: (T) -> Unit,
        errorBlock: ((V?) -> Unit?)? = null,
        errorWithCodeBlock: ((Int,V?) -> Unit?)? = null,
        errorPrinter: NetworkErrorHttpPrinter<V>? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        loadingType: LoadingType = LoadingType.DEFAULT,
        loading: ((Boolean) -> Unit?)? = null
    )

    /**
     * запуск coroutine c применением пользовательской ошибки с бэка
     * Дает возможноть принимать свой тип в ответе ошибки
     * @param block suspend функция
     * @param result результат
     * @param errorBlock блок ошибки (передаем свой тип)
     * @param isPullToRefresh если перезагрузка произошла через свайп тогда передаем true
     */
    fun <T : Any, V : Any> launchWithError(
        block: suspend () -> ResultApi<T>,
        result: (T?) -> Unit,
        errorBlock: ((V?) -> Unit?)? = null,
        errorWithCodeBlock: ((Int,V?) -> Unit?)? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        loadingType: LoadingType = LoadingType.DEFAULT,
        loading: ((Boolean) -> Unit?)? = null
    )


    /**
     * Обработчик ошибок для viewModel используеться для того чтобы обработать ошибку с пользовательской model
     * @param isPullToRefresh если перезагрузка произошла через свайп тогда передаем true
     * @param result результат
     * @param successBlock получание результата (возврашает тип)
     * @param errorBlock получание ошибки (Возврашает тип переданный в [launchWithError])
     */
    suspend fun <T : Any, V : Any> unwrapWithError(
        loadingType: LoadingType = LoadingType.DEFAULT,
        result: ResultApi<T>,
        successBlock: (T?) -> Unit,
        errorBlock: ((V) -> Unit?)? = null,
        errorWithCodeBlock: ((Int,V) -> Unit?)? = null,
        loading: ((Boolean) -> Unit?)? = null
    )


    /**
     * запуск coroutine c применением пользовательской ошибки с бэка
     * Дает возможноть принимать свой тип в ответе ошибки
     * @param isPullToRefresh если перезагрузка произошла через свайп тогда передаем true
     * @param block suspend функция
     * @param result результат
     * @param errorBlock блок ошибки с типом String
     */
    suspend fun <T : Any> unwrap(
        loadingType: LoadingType = LoadingType.DEFAULT,
        result: ResultApi<T>,
        successBlock: (T?) -> Unit,
        errorBlock: ((String) -> Unit?)? = null,
        errorWithCodeBlock: ((Int,String) -> Unit?)? = null,
        loading: ((Boolean) -> Unit?)? = null
    )

    /**
     * Выводим сообщение об ошибке
     */
    fun showError(msg: String)

    /**
     * Перенаправление на нужный фрагмент
     * @param [action] отправляем id action-а который приписываеться в nav графе
     * @param [msg] сообщение может быть любого характера
     */
    fun redirectToFragment(@IdRes action: Int, bundle: Bundle? = null)

    /**
     * Выводим сообщение для определенного поля
     * @param errorMessage сообщение которые показываем на UI
     * @param type тип например Type.password (задаем в текущем модуле для определенного поля)
     */
    fun showErrorByType(errorMessage: String?, type: String?)


    /**
     * Выводим сообщение с кодом ошибки
     * @param errorMessage сообщение которые показываем на UI
     * @param code код ошибки
     */
    fun showErrorByCode(errorMessage: String, code: Int)

    /**
     * Показываем loader по типу
     */
    fun showLoadingByType(type: String, isLoading: Boolean)

    /**
     * Очиска coroutine (если спользовать в паре с CoreLaunchViewModel
     * или CoreAndroidLaunchViewModel тогда отчистка происходит автоматически)
     */
    fun clearCoroutine()

    /**
     * Выводим сообщение
     */
    fun showSuccessMessage(msg: String)

}

class CoreCoroutineDelegate : CoreCoroutine, EncryptedPrefDelegate by EncryptedPrefDelegateImpl() {

    private val parentJob = Job()
    override val scope = CoroutineScope(Dispatchers.Main + parentJob)


    private val _statusEventLiveData = MutableLiveData<EventWrapper<Status>>()
    override val statusEventLiveData: LiveData<EventWrapper<Status>>
        get() = _statusEventLiveData


    private val _errorEventLiveData = MutableLiveData<EventWrapper<String>>()
    override val errorEventLiveData: LiveData<EventWrapper<String>>
        get() = _errorEventLiveData


    private val _errorEventByTypeLiveData = MutableLiveData<EventWrapper<UIValidation>>()
    override val errorEventByTypeLiveData: LiveData<EventWrapper<UIValidation>>
        get() = _errorEventByTypeLiveData

    private val _errorEventByCodeLiveData = MutableLiveData<EventWrapper<Pair<Int, String>>>()
    override val errorEventByCodeLiveData: LiveData<EventWrapper<Pair<Int, String>>>
        get() = _errorEventByCodeLiveData

    private val _successMessageEventLiveData = MutableLiveData<EventWrapper<String>>()
    override val successMessageEventLiveData: LiveData<EventWrapper<String>>
        get() = _successMessageEventLiveData

    private val _loadingByTypeEvent = MutableLiveData<EventWrapper<Pair<String, Boolean>>>()
    override val loadingByTypeEvent: LiveData<EventWrapper<Pair<String, Boolean>>>
        get() = _loadingByTypeEvent


    private val _redirectEventFragment = SingleLiveEvent<EventWrapper<Pair<Int, Bundle?>>>()
    override val redirectEventFragment: LiveData<EventWrapper<Pair<Int, Bundle?>>>
        get() = _redirectEventFragment


    override fun <T : Any> launch(
        block: suspend () -> ResultApi<T>,
        result: (T?) -> Unit,
        errorBlock: ((String?) -> Unit?)?,
        errorWithCodeBlock: ((Int, String?) -> Unit?)?,
        dispatcher: CoroutineDispatcher,
        loadingType: LoadingType,
        loading: ((Boolean) -> Unit?)?
    ) {
        scope.launch(dispatcher) {
            if (loading == null)
                showLoading(loadingType)
            else
                loading.invoke(true)
            val value = block()
            unwrap(loadingType, value, {
                result(it)
            }, errorBlock, errorWithCodeBlock, loading)
        }
    }


    override fun <T> launchFlow(
        requestFlow: suspend () -> Flow<T>,
        result: (T) -> Unit,
        errorBlock: ((String?) -> Unit?)?,
        errorWithCodeBlock: ((Int, String?) -> Unit?)?,
        dispatcher: CoroutineDispatcher,
        loadingType: LoadingType,
        loading: ((Boolean) -> Unit?)?
    ) {
        scope.launch {
            requestFlow.invoke()
                .onStart {
                    if (loading == null)
                        showLoading(loadingType)
                    else
                        loading.invoke(true)
                }
                .handleError { code, message ->
                    when{
                        errorBlock != null -> errorBlock.invoke(message)
                        errorWithCodeBlock != null ->  errorWithCodeBlock.invoke(code, message)
                        else ->{
                            _errorEventLiveData.value = EventWrapper(message)
                            _errorEventByCodeLiveData.value = EventWrapper(code to message)
                        }
                    }
                    if (code == HttpsURLConnection.HTTP_UNAUTHORIZED
                    ) {
                        withContext(Dispatchers.Main) {
                            _statusEventLiveData.value = EventWrapper(Status.REDIRECT_LOGIN)
                        }
                    }
                }
                .onCompletion {
                    if (loading == null)
                        hideLoadingError(loadingType)
                    else
                        loading.invoke(false)

                }
                .collect {
                    result(it)
                }
        }
    }

//    .handleTypeError<V>({ code, message ->
//
//
//    }, ErrorHttpResponse())

    override fun <T : Any, V : Any> launchFlowWithError(
        requestFlow: suspend () -> Flow<T>,
        result: (T) -> Unit,
        errorBlock: ((V?) -> Unit?)?,
        errorWithCodeBlock: ((Int, V?) -> Unit?)?,
        errorPrinter: NetworkErrorHttpPrinter<V>?,
        dispatcher: CoroutineDispatcher,
        loadingType: LoadingType,
        loading: ((Boolean) -> Unit?)?
    ) {
        scope.launch {
            requestFlow.invoke()
                .onStart {
                    if (loading == null)
                        showLoading(loadingType)
                    else
                        loading.invoke(true)
                }.handleTypeError({ code, message ->
                    if (message is ResultApi.HttpError<*>) {
                        val msg = (message.error as? V) ?: return@handleTypeError
                        when{
                            errorBlock != null ->  errorBlock.invoke(msg)
                            errorWithCodeBlock != null -> errorWithCodeBlock.invoke(message.code,msg)
                         }
                        if (code == HttpsURLConnection.HTTP_UNAUTHORIZED
                        ) {
                            withContext(Dispatchers.Main) {
                                _statusEventLiveData.value = EventWrapper(Status.REDIRECT_LOGIN)
                            }
                        }
                    }
                }, errorPrinter)

                .onCompletion {
                    if (loading == null)
                        hideLoadingError(loadingType)
                    else
                        loading.invoke(false)

                }
                .collect {
                    result(it)
                }
        }
    }

    override fun <T : Any, V : Any> launchWithError(
        block: suspend () -> ResultApi<T>,
        result: (T?) -> Unit,
        errorBlock: ((V?) -> Unit?)?,
        errorWithCodeBlock: ((Int, V?) -> Unit?)?,
        dispatcher: CoroutineDispatcher,
        loadingType: LoadingType,
        loading: ((Boolean) -> Unit?)?
    ) {
        scope.launch(dispatcher) {
            if (loading == null)
                showLoading(loadingType)
            else
                loading.invoke(true)
            val value = block()
            unwrapWithError(loadingType, value, {
                result(it)
            }, errorBlock,errorWithCodeBlock, loading)
        }

    }


    override suspend fun <T : Any, V : Any> unwrapWithError(
        loadingType: LoadingType,
        result: ResultApi<T>,
        successBlock: (T?) -> Unit,
        errorBlock: ((V) -> Unit?)?,
        errorWithCodeBlock: ((Int, V) -> Unit?)?,
        loading: ((Boolean) -> Unit?)?
    ) {
        when (result) {
            is ResultApi.Success -> {
                hideLoadingSuccess(loadingType)
                successBlock(result.data)
            }
            is ResultApi.HttpError<*> -> {
                val error = (result.error as? V) ?: return

                try {
                    when {
                        errorBlock != null -> errorBlock.invoke(error)
                        errorWithCodeBlock != null -> errorWithCodeBlock.invoke(result.code, error)
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace()
                    }
                }

                /**
                 * Если приходит код 401 и ты имеем токен
                 * отправляем в стутус редирект в экран логина или запрос нового токена
                 */
                if (result.code == HttpsURLConnection.HTTP_UNAUTHORIZED /*&& !getPref().getAccessToken()
                        .isNullOrEmpty()*/
                ) {
                    withContext(Dispatchers.Main) {
                        _statusEventLiveData.value = EventWrapper(Status.REDIRECT_LOGIN)
                    }
                    return
                }

                /**
                 * В случае ошибки сервера получаем статус ошибки
                 */
                if (loading == null)
                    hideLoadingSuccess(loadingType)
                else
                    loading.invoke(false)
            }
        }
    }

    override suspend fun <T : Any> unwrap(
        loadingType: LoadingType,
        result: ResultApi<T>,
        successBlock: (T?) -> Unit,
        errorBlock: ((String) -> Unit?)?,
        errorWithCodeBlock: ((Int, String) -> Unit?)?,
        loading: ((Boolean) -> Unit?)?
    ) {
        when (result) {
            is ResultApi.Success -> {

                if (loading == null)
                    hideLoadingSuccess(loadingType)
                else
                    loading.invoke(false)

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

                when{
                    errorBlock != null -> errorBlock.invoke(error)
                    errorWithCodeBlock != null -> errorWithCodeBlock.invoke(result.code, error)
                    else ->{
                        _errorEventLiveData.value = EventWrapper(error)
                        _errorEventByCodeLiveData.value = EventWrapper(result.code to error)
                    }
                }

                /**
                 * Если приходит код 401 и ты имеем токен
                 * отправляем в стутус редирект в экран логина или запрос нового токена
                 */
                if (result.code == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                    withContext(Dispatchers.Main) {
                        _statusEventLiveData.value = EventWrapper(Status.REDIRECT_LOGIN)
                    }
                    return
                }

                /**
                 * В случае ошибки сервера получаем статус ошибки
                 */
                if (loading == null)
                    hideLoadingSuccess(loadingType)
                else
                    loading.invoke(false)
            }
        }
    }

    override fun showError(msg: String) {
        _errorEventLiveData.value = EventWrapper(msg)
    }

    override fun showErrorByType(errorMessage: String?, type: String?) {
        _errorEventByTypeLiveData.value =
            EventWrapper(UIValidation(type.orEmpty(), errorMessage.orEmpty()))

    }

    override fun showLoadingByType(type: String, isLoading: Boolean) {
        _loadingByTypeEvent.value = EventWrapper(Pair(type, isLoading))
    }

    override fun showSuccessMessage(msg: String) {
        _successMessageEventLiveData.value = EventWrapper(msg)
    }

    override fun redirectToFragment(action: Int, bundle: Bundle?) {
        _redirectEventFragment.value = EventWrapper(Pair(action, bundle))
    }

    override fun showErrorByCode(errorMessage: String, code: Int) {
        _errorEventByCodeLiveData.value = EventWrapper(code to errorMessage)
    }

    override fun clearCoroutine() {
        parentJob.cancelChildren()
    }


    /**
     * Запуск live data для показа лоадера
     * @param loadingType тип загрузчика
     */
    private suspend fun showLoading(loadingType: LoadingType) {
        withContext(Dispatchers.Main) {
            _statusEventLiveData.value = when (loadingType) {
                LoadingType.DEFAULT -> {
                    EventWrapper(Status.SHOW_LOADING)
                }

                LoadingType.PAGGING -> {
                    EventWrapper(Status.SHOW_PAGGING_LOADING)
                }
                LoadingType.PULL_TO_REFRESH -> {
                    EventWrapper(Status.SHOW_PULL_TO_REFRESH_LOADING)
                }
                LoadingType.NONE -> {
                    return@withContext
                }
            }
        }
    }

    /**
     * Скрываем лоадер при успешном запросе
     * @param loadingType тип загрузчика
     */
    private suspend fun hideLoadingSuccess(loadingType: LoadingType) {
        withContext(Dispatchers.Main) {
            _statusEventLiveData.value = when (loadingType) {
                LoadingType.DEFAULT -> {
                    EventWrapper(Status.HIDE_LOADING)
                }

                LoadingType.PAGGING -> {
                    EventWrapper(Status.HIDE_PAGGING_LOADING)
                }
                LoadingType.PULL_TO_REFRESH -> {
                    EventWrapper(Status.HIDE_PULL_TO_REFRESH_LOADING)
                }
                LoadingType.NONE -> {
                    return@withContext
                }
            }
        }
    }

    /**
     * Скрываем лоадер при ошибки запроса
     * @param loadingType тип загрузчика
     */
    private suspend fun hideLoadingError(loadingType: LoadingType) {
        withContext(Dispatchers.Main) {
            _statusEventLiveData.value = when (loadingType) {
                LoadingType.DEFAULT -> {
                    EventWrapper(Status.HIDE_LOADING)
                }
                LoadingType.PAGGING -> {
                    EventWrapper(Status.HIDE_PAGGING_LOADING)
                }
                LoadingType.PULL_TO_REFRESH -> {
                    EventWrapper(Status.HIDE_PULL_TO_REFRESH_LOADING)
                }
                LoadingType.NONE -> {
                    return@withContext
                }
            }
            _statusEventLiveData.value = EventWrapper(Status.ERROR)
        }
    }
}