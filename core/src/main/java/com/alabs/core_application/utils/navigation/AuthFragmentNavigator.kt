package com.alabs.core_application.utils.navigation

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import com.alabs.core_application.R
import com.alabs.core_application.data.constants.CoreConstant.ARG_AUTH_IS_FROM_IGNORE_FLOW_BLOCKING
import com.alabs.core_application.domain.auth.useCase.CoreIsPendingAuthorizationPassedUseCase
import com.alabs.core_application.utils.extensions.showModuleActivity
import org.koin.core.KoinComponent
import org.koin.core.inject


@Navigator.Name("coreFragment")
class AuthFragmentNavigator(
    private val activity: FragmentActivity,
    manager: FragmentManager,
    containerId: Int,
    private val pathAuthActivity : String
) : FragmentNavigator(activity.baseContext, manager, containerId), KoinComponent {

    /**
     * Сохраняем данные для передачи значений в [goPendingFragment]
     */
    companion object {
        var destinationPending: Destination? = null
        var argsPending: Bundle? = null
        var navOptionsPending: NavOptions? = null
        var navigatorExtrasPending: Navigator.Extras? = null
        var vieId: Int? = -1
    }

    private val isPendingAuthorizationPassedUseCase by inject<CoreIsPendingAuthorizationPassedUseCase>()

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        val authDestination = destination as? AuthFragmentDestination

        /**
         * Значение атрибута нуждаеться ли фрагмент в авторизации
         */
        val isAuthFragment = authDestination?.isAuthFragment() ?: false

        /**
         * В случае если операция перехода требуеться из другого графа значение не может быть -1
         */
        val viewId = authDestination?.getViewId() ?: -1

        /**
         * Делаем исключение по блокированию flow graph-а для isAuthFragment
         */
        val isFlowBlockingException = authDestination?.isFlowBlockingException() ?: false

        /**
         * Если AccessToken пустой, тогда останавливаем переход на другой фрагмент, и записываем данные
         * в статичные переменные, для того чтобы перейти после авторизации
         */
        if (!isPendingAuthorizationPassedUseCase.execute() && isAuthFragment) {
            val authActivityArgs = bundleOf(
                ARG_AUTH_IS_FROM_IGNORE_FLOW_BLOCKING to isFlowBlockingException
            )
            addPendingData(destination, args, navOptions, navigatorExtras, viewId)

            /**
             * Если isFlowBlockingException то не блокируем флоу навигации + отправляем признак что это isFlowBlockingException.
             * На клиенте этот признак можно получить по ключу ARG_AUTH_IS_FROM_IGNORE_FLOW_BLOCKING
             */

            activity.showModuleActivity(
                pathAuthActivity,
                authActivityArgs
            )

            if(isFlowBlockingException) {
                return super.navigate(destination, args, navOptions, navigatorExtras)
            }

            return null
        }

        return super.navigate(destination, args, navOptions, navigatorExtras)
    }


    override fun createDestination(): Destination = AuthFragmentDestination(this)


    private fun addPendingData(
        destination: Destination?,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?,
        viewId: Int?
    ) {
        destinationPending = destination
        argsPending = args
        navOptionsPending = navOptions
        navigatorExtrasPending = navigatorExtras
        vieId = viewId
    }

    fun clearPendingData() {
        destinationPending = null
        argsPending = null
        navOptionsPending = null
        navigatorExtrasPending = null
        AuthNavHostFragment.isUserSuccessAuthorization = false
        vieId = null
    }

    @NavDestination.ClassType(Fragment::class)
    open class AuthFragmentDestination(fragmentNavigator: Navigator<out Destination>) :
        FragmentNavigator.Destination(fragmentNavigator) {

        private var viewId = -1
        private var isAuthFragment = false
        private var isFlowBlockingException = false

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)

            val a = context.resources.obtainAttributes(
                attrs,
                R.styleable.AuthFragmentNavigator
            )
            val className = a.getString(R.styleable.AuthFragmentNavigator_android_name)
            isAuthFragment = a.getBoolean(R.styleable.AuthFragmentNavigator_authFragment, false)
            isFlowBlockingException = a.getBoolean(R.styleable.AuthFragmentNavigator_flowBlockingException, false)
            viewId = a.getResourceId(
                R.styleable.AuthFragmentNavigator_viewId,
                -1
            )
            className?.let { setClassName(it) }
            a.recycle()
        }

        /**
         * Являеться ли фрагмент авторизаванным
         */
        fun isAuthFragment() = isAuthFragment

        /**
         * Делаем исключение по блокированию flow graph-а для isAuthFragent
         */
        fun isFlowBlockingException() = isFlowBlockingException

        /**
         * id контейнера в котором находиться граф
         */
        fun getViewId() = viewId
    }
}