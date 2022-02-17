package com.alabs.core_application.utils.extensions

import com.alabs.KDispatcher.KDispatcherEventConstant.SUCCESS_AUTH_CORE_EVENT
import com.alabs.KDispatcher.call
import com.alabs.core_application.presentation.ui.fragments.CoreFragment

/**
 * При успешной авторизации уведомляем навигаю что пользователь авторизован
 */
fun CoreFragment.successAuth() {
    activity?.finish()
    call(SUCCESS_AUTH_CORE_EVENT)
}

