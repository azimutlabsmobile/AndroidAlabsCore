package com.alabs.core_application.presentation.ui.dialogs

import androidx.fragment.app.DialogFragment
import com.alabs.core_application.data.constants.CoreConstant
import com.alabs.core_application.data.constants.CoreVariables
import com.alabs.core_application.data.network.Status
import com.alabs.core_application.presentation.model.UIValidation
import com.alabs.core_application.utils.callback.PermissionHandler
import com.alabs.core_application.utils.callback.ResultLiveDataHandler
import com.alabs.core_application.utils.extensions.showActivityAndClearBackStack
import com.alabs.core_application.utils.wrappers.EventObserver


open class CoreDialogFragment : DialogFragment(),
    PermissionHandler, ResultLiveDataHandler {

    /**
     * Для того чтобы отслеживать статусы необходимо подписаться во Fragment-е
     */
    protected val statusObserver = EventObserver<Status> {
        it?.let {
            when (it) {
                Status.SHOW_LOADING -> showLoader()
                Status.HIDE_LOADING -> hideLoader()
                Status.REDIRECT_LOGIN -> redirectLogin()
                Status.SHOW_PULL_TO_REFRESH_LOADING -> showPullToRefreshLoader()
                Status.HIDE_PULL_TO_REFRESH_LOADING -> hidePullToRefreshLoader()
                Status.SUCCESS -> success()
                else -> return@let
            }
        }
    }

    protected val errorMessageObserver = EventObserver<String> {
        error(it)
    }

    /**
     * Подписка на ошибки
     * Возврашает строку и тип ошибки, удобно когда нужно вывести ошибку для конкретного случая
     */
    protected val errorMessageByTypeObserver = EventObserver<UIValidation> {
        errorByType(type = it.type, msg = it.message)
    }

    private fun redirectLogin() =
        activity?.showActivityAndClearBackStack(CoreVariables.LOGIN_ACTIVITY)

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
}