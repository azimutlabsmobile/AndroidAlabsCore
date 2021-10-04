package com.alabs.core_application.utils.extensions

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.alabs.KDispatcher.KDispatcherEventConstant.SUCCESS_AUTH_CORE_EVENT
import com.alabs.KDispatcher.call
import com.alabs.core_application.presentation.ui.fragments.CoreFragment
import com.alabs.core_application.utils.navigation.AuthActivityNavigator
import com.alabs.core_application.utils.navigation.AuthFragmentNavigator
import com.alabs.core_application.utils.navigation.AuthNavHostFragment.Companion.isUserSuccessAuthorization

/**
 * При успешной авторизации уведомляем навигаю что пользователь авторизован
 */
fun CoreFragment.successAuth() {
    isUserSuccessAuthorization = true
    activity?.finish()
    call(SUCCESS_AUTH_CORE_EVENT)
}

/**
 * Функция работает в методе onResume fragment-a в паре с [AuthFragmentNavigator]
 * [AuthFragmentNavigator] перехватывает переход на фрагмент если данный фрагмент являеться авторизованным
 * то просте успешной авторизации отправляем пользователя на фрагмент перехваченый [AuthFragmentNavigator]
 */
fun Fragment.goPendingFragment() {
    /**
     * Проверяем успешно ли прошла авторизация за  это отвечает [successAuth]
     */
    if (isUserSuccessAuthorization) {
        /**
         * В случае если нужно перейти в другой навигационный граф тогда передает id контейнера фрагмента
         * Если передача viewId являеться пустой то по умолчанию задаеться значение -1
         * в этом случае операция производиться в текушем графе
         */
        val viewId = AuthFragmentNavigator.vieId ?: -1
        val navController = if (viewId == -1) {
            findNavController()
        } else {
            activity?.findNavController(viewId) ?: return
        }

        /**
         * Находим текущий навигат для отчиски данных
         */
        val navigator =
            navController.navigatorProvider.getNavigator(AuthFragmentNavigator::class.java)
        val destinationId = AuthFragmentNavigator.destinationPending?.id ?: -1

        if(destinationId == -1){
            return
        }

        navController.navigate(
            destinationId,
            AuthFragmentNavigator.argsPending,
            AuthFragmentNavigator.navOptionsPending,
            AuthFragmentNavigator.navigatorExtrasPending
        )
        /**
         * Очишаем данные
         */
        navigator.clearPendingData()
    }
}





/**
 * Функция работает в методе onResume fragment-a в паре с [AuthActivityNavigator]
 * [AuthActivityNavigator] перехватывает переход на активити если данный фрагмент являеться авторизованным
 * то просте успешной авторизации отправляем пользователя на activity перехваченное [AuthActivityNavigator]
 */
fun Activity.goPendingActivity() {
    if (isUserSuccessAuthorization) {
        val viewId = AuthActivityNavigator.vieId ?: -1
        if (viewId == -1) {
            return
        }
        /**
         * Находим текущий навигат для отчиски данных
         */
        val navigator =
            findNavController(viewId).navigatorProvider.getNavigator(AuthActivityNavigator::class.java)
        val destinationId = AuthActivityNavigator.destinationPending?.id ?: -1

        if(destinationId == -1){
            return
        }

        findNavController(viewId).navigate(
            destinationId,
            AuthActivityNavigator.argsPending,
            AuthActivityNavigator.navOptionsPending,
            AuthActivityNavigator.navigatorExtrasPending
        )
        /**
         * Очишаем данные
         */
        navigator.clearPendingData()
    }
}

