package com.alabs.core_application.utils.navigation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.alabs.core_application.R
import com.alabs.core_application.domain.auth.useCase.CoreIsPendingAuthorizationPassedUseCase
import com.alabs.core_application.utils.extensions.showModuleActivity
import org.koin.core.KoinComponent
import org.koin.core.inject

@Navigator.Name("coreActivity")
class AuthActivityNavigator(
    private val context: Context,
    private val pathAuthActivity: String
) :
    ActivityNavigator(context), KoinComponent {

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
        /**
         * Значение атрибута нуждаеться ли фрагмент в авторизации
         */
        val authDestination = destination as? AuthActivityDestination

        /**
         * В случае если операция перехода требуеться из другого графа значение не может быть -1
         */
        val isAuthActivity = authDestination?.isAuthActivity() ?: false

        /**
         * Если AccessToken пустой, тогда останавливаем переход на другой фрагмент, и записываем данные
         * в статичные переменные, для того чтобы перейти после авторизации
         */
        val viewId = authDestination?.getViewId() ?: -1
        val activity = context as? Activity
        if (!isPendingAuthorizationPassedUseCase.execute() && isAuthActivity) {
            activity?.showModuleActivity(pathAuthActivity)
            addPendingData(destination, args, navOptions, navigatorExtras, viewId)
            return null
        }

        return super.navigate(destination, args, navOptions, navigatorExtras)
    }

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


    override fun createDestination() = AuthActivityDestination(this)

    @NavDestination.ClassType(Activity::class)
    open class AuthActivityDestination(activityNavigator: Navigator<out Destination>) :
        ActivityNavigator.Destination(activityNavigator) {


        private var isAuthActivity = false
        private var viewId = -1

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)

            val a = context.resources.obtainAttributes(
                attrs,
                R.styleable.AuthActivityNavigator
            )
            isAuthActivity = a.getBoolean(
                R.styleable.AuthActivityNavigator_authActivity,
                false
            )
            viewId = a.getResourceId(
                R.styleable.AuthActivityNavigator_viewId,
                -1
            )
            a.recycle()
        }


        fun isAuthActivity() = isAuthActivity

        fun getViewId() = viewId
    }
}