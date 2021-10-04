package com.alabs.core_application.presentation.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.alabs.KDispatcher.IKDispatcher
import com.alabs.KDispatcher.KDispatcherEventConstant
import com.alabs.KDispatcher.subscribe
import com.alabs.KDispatcher.unsubscribe
import com.alabs.core_application.data.constants.CoreConstant.PERMISSION_DENIED
import com.alabs.core_application.data.constants.CoreVariables.LOGIN_ACTIVITY
import com.alabs.core_application.data.network.Status
import com.alabs.core_application.presentation.model.UIValidation
import com.alabs.core_application.presentation.viewModel.CoreAuthViewModel
import com.alabs.core_application.utils.callback.PermissionHandler
import com.alabs.core_application.utils.callback.ResultLiveDataHandler
import com.alabs.core_application.utils.extensions.goPendingFragment
import com.alabs.core_application.utils.extensions.showActivityAndClearBackStack
import com.alabs.core_application.utils.wrappers.EventObserver
import org.koin.android.viewmodel.ext.android.viewModel
import java.net.HttpURLConnection.*

/**
 * Фрагмент для авторизованой зоны, в случаем ели не пребуеться сразу переходить
 * в авторизовый фрагмент передаем  [isGoToPendingFragment] false
 */
abstract class CoreFragment(
    id: Int,
    private val isGoToPendingFragment: Boolean = true,
    private val isAuthCallBack: Boolean = false
) :
    Fragment(id), ResultLiveDataHandler, PermissionHandler, IKDispatcher {


    private val viewModel by viewModel<CoreAuthViewModel>()

    /**
     * Для того чтобы отслеживать статусы необходимо подписаться во Fragment-е
     */
    protected val statusObserver = EventObserver<Status> {
        it.let {
            when (it) {
                Status.SHOW_LOADING -> showLoader()
                Status.HIDE_LOADING -> hideLoader()
                Status.REDIRECT_LOGIN -> redirectLogin()
                Status.SHOW_PULL_TO_REFRESH_LOADING -> showPullToRefreshLoader()
                Status.HIDE_PULL_TO_REFRESH_LOADING -> hidePullToRefreshLoader()
                Status.SHOW_PAGGING_LOADING -> showPagingLoader()
                Status.HIDE_PAGGING_LOADING -> hidePagingLoader()
                Status.SUCCESS -> success()
                else -> return@let
            }
        }
    }


    protected val loaderByTypeObserver = EventObserver<Pair<String, Boolean>> {
        val (type, isLoading) = it
        if (isLoading) {
            showLoaderByType(type)
        } else {
            hideLoaderByType(type)
        }
    }

    protected val errorMessageObserver = EventObserver<String> {
        error(it)
    }

    protected val successMessageObserver = EventObserver<String> {
        successMessage(it)
    }

    /**
     * Подписка на ошибки
     * Возврашает строку и тип ошибки, удобно когда нужно вывести ошибку для конкретного случая
     */
    protected val errorMessageByTypeObserver = EventObserver<UIValidation> {
        errorByType(type = it.type, msg = it.message)
    }


    /**
     * Используем чтобы отследить конкретную ошибку
     */
    protected val errorMessageByCodeObserver = EventObserver<Pair<Int, String>> {
       val (code , error) = it
       when(code){
           HTTP_INTERNAL_ERROR -> on500Error(error)
           HTTP_NOT_FOUND  ->  on404Error(error)
       }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeAuthCallBack(view, savedInstanceState)

        if (viewModel.isPendingAuthorizationPassed) {
            onAuthorizedViewCreated(view, savedInstanceState)
        } else {
            onUnAuthorizedViewCreated(view, savedInstanceState)
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
    open fun onAuthorizedViewCreated(view: View, savedInstanceState: Bundle?) {
        // do nothing
    }

    /**
     * выполняться код для авторизованных пользователей
     */
    open fun onUnAuthorizedViewCreated(view: View, savedInstanceState: Bundle?) {
        // do nothing
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.forEach {
            when {
                it != PERMISSION_DENIED -> {
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

    override fun onDestroyView() {
        super.onDestroyView()
        unsubscribeAuthCallBack()
    }


    override fun onResume() {
        super.onResume()
        if (isGoToPendingFragment) {
            goPendingFragment()
        }

    }


    open fun redirectLogin() = activity?.showActivityAndClearBackStack(LOGIN_ACTIVITY)

    /*
    * Проверка авторизован ли ожидающий подтверждения авторизации пользователь
    */
    open fun isPendingAuthorizationPassed(): Boolean = viewModel.isPendingAuthorizationPassed

    /**
     * При успешной авторизации пересоздаем состоние
     */
    private fun subscribeAuthCallBack(view: View, savedInstanceState: Bundle?) {
        if (isAuthCallBack) {
            subscribe<Unit>(KDispatcherEventConstant.SUCCESS_AUTH_CORE_EVENT, 1) {
                onAuthorizedViewCreated(view, savedInstanceState)
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