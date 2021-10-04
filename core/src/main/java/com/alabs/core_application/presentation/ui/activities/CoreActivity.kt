package com.alabs.core_application.presentation.ui.activities

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alabs.KDispatcher.IKDispatcher
import com.alabs.KDispatcher.KDispatcherEventConstant
import com.alabs.KDispatcher.subscribe
import com.alabs.KDispatcher.unsubscribe
import com.alabs.core_application.data.constants.CoreConstant
import com.alabs.core_application.data.constants.CoreVariables.ACTIVITIES_SCREEN_ORIENTATION
import com.alabs.core_application.data.network.Status
import com.alabs.core_application.presentation.model.UIValidation
import com.alabs.core_application.presentation.viewModel.CoreAuthViewModel
import com.alabs.core_application.utils.callback.PermissionHandler
import com.alabs.core_application.utils.callback.ResultLiveDataHandler
import com.alabs.core_application.utils.delegates.*
import com.alabs.core_application.utils.extensions.toast
import com.alabs.core_application.utils.wrappers.EventObserver
import org.koin.android.viewmodel.ext.android.viewModel
import java.net.HttpURLConnection


abstract class CoreActivity(
    lay: Int,
    private val isAuthCallBack: Boolean = false
) : AppCompatActivity(lay), ResultLiveDataHandler,
    DarkTheme by DarkThemeDelegate(),
    TransitionAnimation by TransitionAnimationActivityDelegate(), PermissionHandler, IKDispatcher {

    private val viewModel by viewModel<CoreAuthViewModel>()

    protected val errorMessageObserver = EventObserver<String> { toast(it) }

    protected val errorMessageByTypeObserver = EventObserver<UIValidation> {
        errorByType(type = it.type, msg = it.message)
    }

    protected val successMessageObserver = EventObserver<String> {
        successMessage(it)
    }


    protected val loaderByTypeObserver = EventObserver<Pair<String, Boolean>> {
        val (type, isLoading) = it
        if (isLoading) {
            showLoaderByType(type)
        } else {
            hideLoaderByType(type)
        }
    }

    /**
     * Используем чтобы отследить конкретную ошибку
     */
    protected val errorMessageByCodeObserver = EventObserver<Pair<Int, String>> {
        val (code , error) = it
        when(code){
            HttpURLConnection.HTTP_INTERNAL_ERROR -> on500Error(error)
            HttpURLConnection.HTTP_NOT_FOUND ->  on404Error(error)
        }
    }

    /**
     * Вызываеться после успешной авторизации
     */
    open fun onUpdateStateSuccessAuth() {
        // do nothing
    }

    /**
     * выполняться код для авторизованных пользователей
     */
    open fun onAuthorizedViewCreate(savedInstanceState: Bundle?) {
        // do nothing
    }

    /**
     * выполняться код для авторизованных пользователей
     */
    open fun onUnAuthorizedViewCreate(savedInstanceState: Bundle?) {
        // do nothing
    }

    open fun redirectLogin() {
        // реализовать в случае базовой функциональности
    }

    /**
     * Для того чтобы отслеживать статусы необходимо подписаться в Activity
     */
    protected val statusObserver = EventObserver<Status> {
        it.let {
            when (it) {
                Status.SHOW_LOADING -> showLoader()
                Status.HIDE_LOADING -> hideLoader()
                Status.REDIRECT_LOGIN -> redirectLogin()
                Status.SHOW_PULL_TO_REFRESH_LOADING -> showPullToRefreshLoader()
                Status.HIDE_PULL_TO_REFRESH_LOADING -> hidePullToRefreshLoader()
                Status.SHOW_PAGING_LOADING -> showPagingLoader()
                Status.HIDE_PAGING_LOADING -> hidePagingLoader()
                Status.SUCCESS -> success()
                else -> return@let
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme(window, Theme.DARK)
        initTransition(this)
        /*
        * Если задан то задаем ориентацию для всех активити
        * https://stackoverflow.com/questions/48072438/java-lang-illegalstateexception-only-fullscreen-opaque-activities-can-request-o
        */
        ACTIVITIES_SCREEN_ORIENTATION?.let {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
                requestedOrientation = it
            }
        }

        subscribeAuthCallBack(savedInstanceState)

        if (viewModel.isPendingAuthorizationPassed) {
            onAuthorizedViewCreate(savedInstanceState)
        } else {
            onUnAuthorizedViewCreate(savedInstanceState)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribeAuthCallBack()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.forEach {
            when {
                it != CoreConstant.PERMISSION_DENIED -> {
                    confirmPermission()
                    confirmWithRequestCode(requestCode)
                    return
                }
                else -> {
                    ignorePermission()
                    return
                }
            }
        }
    }

    /**
     * При успешной авторизации пересоздаем состоние
     */
    private fun subscribeAuthCallBack(savedInstanceState: Bundle?) {
        if (isAuthCallBack) {
            subscribe<Unit>(KDispatcherEventConstant.SUCCESS_AUTH_CORE_EVENT, 1) {
                onAuthorizedViewCreate(savedInstanceState)
                onUpdateStateSuccessAuth()
            }
        }
    }

    /**
     * Удаляем callback успешной авторизации
     */
    private fun unsubscribeAuthCallBack() {
        if (isAuthCallBack) {
            unsubscribe(KDispatcherEventConstant.SUCCESS_AUTH_CORE_EVENT)
        }
    }

}

